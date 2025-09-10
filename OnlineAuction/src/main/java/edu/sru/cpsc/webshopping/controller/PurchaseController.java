package edu.sru.cpsc.webshopping.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;

import edu.sru.cpsc.webshopping.domain.billing.Purchase;
import edu.sru.cpsc.webshopping.domain.billing.ShippingAddress;
import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.products.PurchaseRequest;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.service.PurchaseService;
import edu.sru.cpsc.webshopping.service.UserService;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private MarketListingDomainController marketListingController;

    @Autowired
    private UserService userService;

    @Autowired
    private MarketListingRepository marketListingRepository;

    public PurchaseController() {
        Stripe.apiKey = "sk_test_51QF5KhDuIe0xI0k5kokecMv2ASGbBXs4C8HxgD5uDTHGLR73non990TSm3t9jeRQQHMbg6y3XGhRGzrEh4ucfviG00q6h0bY4K";
    }

    @PostMapping
    public String openPurchasePage(@RequestParam("marketListingId") long marketListingId, @RequestParam("qtyBought") int qtyBought, Model model, Principal principal) {
        try {
            User user = userService.getUserByUsername(principal.getName());
            MarketListing marketListing = marketListingController.getMarketListing(marketListingId);

            if (marketListing == null || qtyBought > marketListing.getQtyAvailable()) {
                model.addAttribute("error", "Invalid quantity or listing not found.");
                return "redirect:/homePage";
            }

            BigDecimal pricePerItem = marketListing.getPricePerItem();
            BigDecimal amountInCents = pricePerItem.multiply(BigDecimal.valueOf(qtyBought))
                                                  .multiply(BigDecimal.valueOf(100));
            long amount = amountInCents.longValue();

            PaymentIntent paymentIntent = PaymentIntent.create(
                PaymentIntentCreateParams.builder().setAmount(amount).setCurrency("usd").setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()).build()
            );

            model.addAttribute("clientSecret", paymentIntent.getClientSecret());
            model.addAttribute("marketListing", marketListing);
            model.addAttribute("qtyBought", qtyBought);
            model.addAttribute("amount", amountInCents.divide(BigDecimal.valueOf(100)));

            return "purchase";
        } catch (StripeException e) {
            model.addAttribute("error", "Payment setup failed: " + e.getMessage());
            return "redirect:/homePage";
        }
    }

    @GetMapping("/complete")
    public String completePurchase(
            @RequestParam("paymentIntentId") String paymentIntentId,
            @RequestParam("marketListingId") Long marketListingId,
            @RequestParam("qtyBought") Integer qtyBought,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("streetAddress") String streetAddress,
            @RequestParam("city") String city,
            @RequestParam("state1") String state1,
            @RequestParam("postalCode") String postalCode,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Find and verify the listing
            MarketListing listing = marketListingRepository.findById(marketListingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

            if (listing.getQtyAvailable() < qtyBought) {
                redirectAttributes.addFlashAttribute("error", "Insufficient quantity available");
                return "redirect:/homePage";
            }

            // Create shipping address record
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setFullName(fullName);
            shippingAddress.setPhone(phone);
            shippingAddress.setStreetAddress(streetAddress);
            shippingAddress.setCity(city);
            shippingAddress.setState1(state1);
            shippingAddress.setPostalCode(postalCode);
           
            // Create order record
            
            Purchase purchase = new Purchase();
            purchase.setPaymentIntentId(paymentIntentId);
            purchase.setMarketListing(listing);
            purchase.setQuantity(qtyBought);
            purchase.setShippingAddress(shippingAddress);
            purchase.setOrderDate(LocalDateTime.now());
            purchase.setStatus("PAID");
            
            // Update the quantity
            listing.setQtyAvailable(listing.getQtyAvailable() - qtyBought);
            marketListingRepository.save(listing);

            return "redirect:/purchase/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process purchase: " + e.getMessage());
            return "redirect:/homePage";
        }
    }
    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }
}