package edu.sru.cpsc.webshopping.controller;

import org.springframework.http.ResponseEntity;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.market.Transaction;
import edu.sru.cpsc.webshopping.domain.misc.Notification;
import edu.sru.cpsc.webshopping.domain.user.Applicant;
import edu.sru.cpsc.webshopping.domain.user.Message;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.domain.user.UserList;
import edu.sru.cpsc.webshopping.domain.widgets.Widget;
import edu.sru.cpsc.webshopping.domain.widgets.WidgetImage;
import edu.sru.cpsc.webshopping.repository.applicant.ApplicantRepository;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.misc.NotificationRepository;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import edu.sru.cpsc.webshopping.repository.widgets.WidgetImageRepository;
import edu.sru.cpsc.webshopping.repository.widgets.WidgetRepository;
import edu.sru.cpsc.webshopping.service.CategoryService;
import edu.sru.cpsc.webshopping.service.NotificationService;
import edu.sru.cpsc.webshopping.service.TicketService;
import edu.sru.cpsc.webshopping.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import edu.sru.cpsc.webshopping.service.MarketListingService;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.repository.market.OfferNotificationRepository;

/**
 * Controller for Home page and searching. Interacts with the widget database
 * and its inheriting classes.
 *
 * @author Grant Riley
 * @version 2.0
 * @since version 1.0
 */
@Controller
public class LandingPageController {

	@Autowired
	private UserService userService;

	@Autowired
	private MarketListingService marketService;

	@Autowired
	private MarketListingService marketListingService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	WidgetImageRepository widgetImageRepository;

	@Autowired
	private OfferNotificationRepository offerNotificationRepository;
	@Autowired
	private MarketListingRepository marketListingRepository;

	UserRepository userRepository;
	WidgetController widgetController;
	MarketListingDomainController marketController;
	UserListDomainController userListController;

	EmailController emailController;
	private List<User> allUsers = new ArrayList<>();
	private TransactionController transController;
	private UserController userController;
	private String page;
	private String page2;
	private String page3;
	private String masterPage;
	private List<Widget> allWidgets = new ArrayList<>();
	private List<List<WidgetImage>> allWidgetImages = new ArrayList<>();
	private List<String> allEncodedImages = new ArrayList<>();
	private List<User> allSellers = new ArrayList<>();
	private List<Message> allMessages = new ArrayList<>();
	public String search;
	List<String> roleList;
	private int count = 0;
	public String filter;
	public String defaultFilter = "----";
	private List<MarketListing> allMarketListings = new ArrayList<>();
	private final Logger log = LoggerFactory.getLogger(getClass());

	LandingPageController(UserRepository userRepository, WidgetController widgetController,
			MarketListingDomainController marketController, TransactionController transController,
			UserController userController, UserListDomainController userListController,
			EmailController emailController) {
		this.userRepository = userRepository;
		this.widgetController = widgetController;
		this.marketController = marketController;
		this.transController = transController;
		this.userController = userController;
		this.userListController = userListController;
		this.emailController = emailController;
	}

	@GetMapping({ "/friendsOff" })
	public String friendsOff(Model model, Principal principal) {
		setPage("friendOff");
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);

		return "homePage";
	}

	@GetMapping({ "/blockList" })
	public String blockList(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage("friends");
		setPage3("blockList");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		List<UserList> blocked = userListController.getBlocked(user);
		model.addAttribute("myBlocked", blocked);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("page3", getPage3());
		model.addAttribute("user", user);

		return "homePage";
	}

	@GetMapping({ "/friends" })
	public String friends(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage("friends");
		setPage3("friendList");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		List<UserList> friends = userListController.getFriends(user);
		model.addAttribute("myFriends", friends);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("page3", getPage3());
		model.addAttribute("user", user);

		return "homePage";
	}

	@GetMapping({ "/suggested" })
	public String suggested(Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage("friends");
		setPage3("suggestedList");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		Iterable<MarketListing> listings = marketController.getAllListings();
		model.addAttribute("listings", listings);
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("page3", getPage3());
		model.addAttribute("user", user);

		return "homePage";
	}

	@RequestMapping({ "/friend" })
	public String friend(@RequestParam("friend") String friendName, Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage2("null");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		UserList myList = new UserList();
		myList.setOwner(user);
		List<UserList> friends = userListController.getFriends(user);
		if (userController.getUserByUsername(friendName) != null) {
			User userFriend = userController.getUserByUsername(friendName);

			UserList[] friendsArray = friends.toArray(new UserList[friends.size()]);
			for (int i = 0; i < friendsArray.length; i++) {
				if (friendsArray[i].getFriend().getId() == userFriend.getId()) {

					setPage2("alreadyFriends");
				}
			}
			if (!(getPage2().contains("alreadyFriends"))) {
				userListController.addFriend(myList, userFriend);
				setPage2("newFriend");
			}
		} else {
			setPage2("friendNoExist");
		}
		friends = userListController.getFriends(user);
		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());
		model.addAttribute("myFriends", friends);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping({ "/sendFriendMessage" })
	public String sendFriendMessage(@RequestParam("recipient") String name, @RequestParam("message") String content,
			@RequestParam("subject") String subject, Model model, Principal principal) {

		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		User receiver = userService.getUserByUsername(name);

		model.addAttribute("page", getPage());
		model.addAttribute("user", user);

		if (userController.getUserByUsername(name) == null) {
			setPage2("fail");
			model.addAttribute("page2", getPage2());

			return "homepage";
		}

		if (subject.length() == 0) {
			subject = "<no subject>";
		}
		if (content.length() == 0) {
			content = "<no content>";
		}
		Message message = new Message();
		message.setOwner(user);
		message.setSender(user.getUsername());
		message.setContent(content);
		message.setSubject(subject);
		message.setMsgDate();
		message.setReceiverName(name);
		message.setReceiver(receiver);
		emailController.messageEmail(receiver, user, message);

		setPage2("sent");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);

		List<UserList> friends = userListController.getFriends(user);

		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());
		model.addAttribute("myFriends", friends);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping({ "/addFriend/{id}" })
	public String addAFriend(@PathVariable("id") int id, Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage2("null");
		User friend = userController.getUser(id, model);
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		UserList myList = new UserList();
		myList.setOwner(user);
		List<UserList> friends = userListController.getFriends(user);
		if (userController.getUserByUsername(friend.getUsername()) != null) {

			UserList[] friendsArray = friends.toArray(new UserList[friends.size()]);
			for (int i = 0; i < friendsArray.length; i++) {
				if (friendsArray[i].getFriend().getId() == friend.getId()) {

					setPage2("alreadyFriends");
				}
			}
			if (!(getPage2().contains("alreadyFriends"))) {
				userListController.addFriend(myList, friend);
				setPage2("newFriend");
			}
		} else {
			setPage2("friendNoExist");
		}
		friends = userListController.getFriends(user);
		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());
		model.addAttribute("myFriends", friends);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping({ "/sendMessageToFriend/{id}" })
	public String sendMessageToFriend(@PathVariable("id") int id, Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		if (getPage2() == null || !(getPage2().equals("sendPrep"))) {
			setPage2("sendPrep");
		} else {
			setPage2("null");
		}

		User friend = userController.getUser(id, model);
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		model.addAttribute("friend", friend);

		List<UserList> friends = userListController.getFriends(user);
		List<UserList> blocked = userListController.getBlocked(user);

		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());
		if (getPage3().equals("blockList")) {

			model.addAttribute("myBlocked", blocked);
		} else {
			model.addAttribute("myFriends", friends);
		}
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping({ "/removeFriend/{id}" })
	public String removeFriend(@PathVariable("id") int id, Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage2("removeFriend");
		User friend = userController.getUser(id, model);
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}
		if (getPage3().equals("blockList")) {
			setPage2("removeBlock");
			userListController.removeBlock(user, friend);
			List<UserList> blocked = userListController.getBlocked(user);
			model.addAttribute("myBlocked", blocked);
		} else {
			userListController.removeFriend(user, friend);
			List<UserList> friends = userListController.getFriends(user);
			model.addAttribute("myFriends", friends);
		}
		model.addAttribute("names", allusernames);

		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());

		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping({ "/block" })
	public String block(@RequestParam("block") String blockName, Model model, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		setPage2("null");
		getAllUsers().clear();
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));
		String[] allusernames = new String[getAllUsers().size()];
		for (int i = 0; i < allusernames.length; i++) {
			allusernames[i] = getAllUsers().get(i).getUsername();
		}

		model.addAttribute("names", allusernames);
		UserList myList = new UserList();
		myList.setOwner(user);
		List<UserList> blocked = userListController.getBlocked(user);
		if (userController.getUserByUsername(blockName) != null) {
			User blockUser = userController.getUserByUsername(blockName);

			UserList[] blockArray = blocked.toArray(new UserList[blocked.size()]);
			for (int i = 0; i < blockArray.length; i++) {
				if (blockArray[i].getBlock().getId() == blockUser.getId()) {
					setPage2("alreadyBlocked");
				}
			}
			if (!(getPage2().contains("alreadyBlocked"))) {
				userListController.addBlock(myList, blockUser);
				setPage2("newBlocked");
			}
		} else {
			setPage2("blockedNoExist");
		}
		blocked = userListController.getBlocked(user);
		model.addAttribute("page3", getPage3());
		model.addAttribute("page2", getPage2());
		model.addAttribute("myBlocked", blocked);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		model.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		model.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		model.addAttribute("soldTrans", soldTrans);
		model.addAttribute("sellerRating", user.getSellerRating());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		return "homePage";
	}

	@RequestMapping("homePage")
	public String homePage(Model widgetModel, Model listingModel, String tempSearch, Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		Long userId = user.getId();
		if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_TECHNICALSERVICE")
				|| user.getRole().equals("ROLE_CUSTOMERSERVICE") || user.getRole().equals("ROLE_SALES")
				|| user.getRole().equals("ROLE_HIRINGMANAGER") || user.getRole().equals("ROLE_SECURITY")
				|| user.getRole().equals("ROLE_ADMIN_SHADOW") || user.getRole().equals("ROLE_HELPDESK_ADMIN")
				|| user.getRole().equals("ROLE_HELPDESK_REGULAR")) {

			return "redirect:employee";
		}
		widgetModel.addAttribute("widgets", widgetController.getAllWidgets());
		listingModel.addAttribute("listings", marketController.getAllListings());
		Iterable<Transaction> purchases = transController.getUserPurchases(user);
		listingModel.addAttribute("purchases", purchases);
		Iterable<Transaction> soldTrans = transController.getUserSoldItems(user);
		listingModel.addAttribute("soldTrans", soldTrans);
		listingModel.addAttribute("sellerRating", user.getSellerRating());
		setPage("home");
		listingModel.addAttribute("page", getPage());
		widgetModel.addAttribute("page", getPage());
		listingModel.addAttribute("headerUser", user);
		listingModel.addAttribute("user", user);
		widgetModel.addAttribute("headerUser", user);

		List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(user.getId());
		widgetModel.addAttribute("notifications", notifications);
		notificationService.markAllNotificationsAsReadForUser(userId);

		// TODO:
		// - Loop through and add filter categories from category tree

		return "homePage";
	}

	@PostMapping("/markOfferViewed/{offerId}")
	@ResponseBody
	public ResponseEntity<Void> markOfferViewed(@PathVariable Long offerId, Principal principal) {
		try {
			if (principal == null) {
				return ResponseEntity.badRequest().build();
			}

			User user = userService.getUserByUsername(principal.getName());
			OfferNotification offer = offerNotificationRepository.findById(offerId).orElse(null);

			if (offer != null && user != null) {
				MarketListing listing = marketListingRepository.findById(offer.getMarketListingId()).orElse(null);

				boolean isSeller = listing != null && listing.getSeller().getId() == user.getId();
				boolean isBuyerWithCounterOffer = offer.getPotentialBuyerUserId() == user.getId()
						&& offer.isHasCounterOffer();

				if (isSeller) {
					offer.markOfferAsViewed();
					offerNotificationRepository.save(offer);
					log.info("Marked offer {} as viewed by seller {}", offerId, user.getUsername());
					return ResponseEntity.ok().build();
				} else if (isBuyerWithCounterOffer) {
					offer.markCounterOfferAsViewed();
					offerNotificationRepository.save(offer);
					log.info("Marked counter offer {} as viewed by buyer {}", offerId, user.getUsername());
					return ResponseEntity.ok().build();
				}
			}

			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("Error marking offer as viewed: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

	// Update your browseWidgetsButton method
	@GetMapping("/BrowseWidgetsButton/page/{pageNumber}")
	public String browseWidgetsButton(Model model, Principal principal, @PathVariable("pageNumber") int currentPage)
			throws UnsupportedEncodingException {
		User user = (principal != null) ? userService.getUserByUsername(principal.getName()) : null;

		setPage("searchWidgetSellerMarketListing");
		setMasterPage("query");
		model.addAttribute("masterPage", getMasterPage());

		// Clear all lists
		getAllMarketListings().clear();
		getAllWidgetImages().clear();
		getAllEncodedImages().clear();
		getAllWidgets().clear();
		getAllSellers().clear();
		getAllUsers().clear();

		// Get all users
		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));

		try {
			// Get paginated market listings
			Page<MarketListing> marketListingsPage = marketService.findPage(currentPage);

			// populates lists to ensure size matches
			for (int i = 0; i < marketListingsPage.getContent().size(); i++) {
				getAllEncodedImages().add(null);
			}

			// Process each market listing and its corresponding image
			int index = 0;
			for (MarketListing marketListing : marketListingsPage) {
				getAllMarketListings().add(marketListing);
				getAllWidgets().add(marketListing.getWidgetSold());
				getAllSellers().add(marketListing.getSeller());

				// Handle the cover image
				try {
					WidgetImage coverImage = widgetImageRepository.findFirstByMarketListing(marketListing);
					if (coverImage != null && coverImage.getImageData() != null) {
						Blob imageData = coverImage.getImageData();
						int blobLength = (int) imageData.length();
						String encodedImage = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
						getAllEncodedImages().set(index, encodedImage);
					}
				} catch (SQLException | RuntimeException e) {
					log.error("Error processing image for market listing {}: {}", marketListing.getId(),
							e.getMessage());

				}
				index++;
			}

			// Add pagination information
			int totalPages = marketListingsPage.getTotalPages();
			long totalItems = marketListingsPage.getTotalElements();
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("totalItems", totalItems);

			// Add lists to model
			model.addAttribute("users", getAllUsers());
			model.addAttribute("allMarketListings", getAllMarketListings());
			model.addAttribute("allWidgets", getAllWidgets());
			model.addAttribute("allSellers", getAllSellers());
			model.addAttribute("allEncodedImages", getAllEncodedImages());

		} catch (Exception e) {
			log.error("Error loading market listings: {}", e.getMessage());
			// Initialize empty lists in case of error
			model.addAttribute("allMarketListings", new ArrayList<>());
			model.addAttribute("allEncodedImages", new ArrayList<>());
			model.addAttribute("currentPage", 1);
			model.addAttribute("totalPages", 1);
			model.addAttribute("totalItems", 0);
		}

		// Add remaining model attributes
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		model.addAttribute("filter", "----");
		model.addAttribute("filtered", false);

		// Add attribute values for filters
		try {
			Map<String, List<String>> attributeValues = marketService.getAllUniqueAttributeValues();
			model.addAttribute("yearValues", attributeValues.get("Year"));
			model.addAttribute("conditionValues", attributeValues.get("Condition"));
			model.addAttribute("makeValues", attributeValues.get("Make"));
			model.addAttribute("modelValues", attributeValues.get("Model_car"));
			model.addAttribute("keywordValues", attributeValues.get("Keywords"));
			model.addAttribute("engineValues", attributeValues.get("Engine"));
		} catch (Exception e) {
			log.error("Error loading filter values: {}", e.getMessage());
			// Add empty lists for filters in case of error
			model.addAttribute("yearValues", new ArrayList<>());
			model.addAttribute("conditionValues", new ArrayList<>());
			model.addAttribute("makeValues", new ArrayList<>());
			model.addAttribute("modelValues", new ArrayList<>());
			model.addAttribute("keywordValues", new ArrayList<>());
			model.addAttribute("engineValues", new ArrayList<>());
		}

		return "browseWidgets";
	}

	@GetMapping("/BrowseWidgetsButton/page/{pageNumber}/attributeFiltered")
	public String attributeFilteredBrowsePage(Model model, Principal principal,
			@PathVariable("pageNumber") int currentPage, @RequestParam(required = false) String year,
			@RequestParam(required = false) String condition, @RequestParam(required = false) String make,
			@RequestParam(required = false) String model_car, @RequestParam(required = false) String keywords,
			@RequestParam(required = false) String engine, @RequestParam(required = false) String changedAttribute)
			throws UnsupportedEncodingException {
		User user = (principal != null) ? userService.getUserByUsername(principal.getName()) : null;

		setPage("searchWidgetSellerMarketListing");
		setMasterPage("query");
		model.addAttribute("masterPage", getMasterPage());

		boolean isAnyFilterActive = year != null || condition != null || make != null || model_car != null
				|| keywords != null || engine != null;

		model.addAttribute("isAnyFilterActive", isAnyFilterActive);
		// Clear previous data
		getAllMarketListings().clear();
		getAllWidgetImages().clear();
		getAllEncodedImages().clear();
		getAllWidgets().clear();
		getAllSellers().clear();
		getAllUsers().clear();

		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));

		Map<String, String> selectedFilters = new HashMap<>();
		selectedFilters.put("Year", year);
		selectedFilters.put("Condition", condition);
		selectedFilters.put("Make", make);
		selectedFilters.put("Model_car", model_car);
		selectedFilters.put("Keywords", keywords);
		selectedFilters.put("Engine", engine);

		// Get updated attribute values based on the changed attribute
		Map<String, List<String>> updatedAttributeValues = marketService.getUpdatedAttributeValues(selectedFilters,
				changedAttribute);

		// Apply filters and get paginated results
		Page<MarketListing> marketListingsPage = marketService.findPageWithAttributeFilter(currentPage, year, condition,
				make, model_car, keywords, engine);

		// Process market listings
		for (MarketListing marketListing : marketListingsPage) {
			getAllMarketListings().add(marketListing);
			getAllWidgets().add(marketListing.getWidgetSold());
			getAllSellers().add(marketListing.getSeller());

			WidgetImage coverImage = widgetImageRepository.findFirstByMarketListing(marketListing);
			if (coverImage != null) {
				String imageString = encodeImage(coverImage.getImageData());
				getAllEncodedImages().add(imageString);
			}
		}

		// Set pagination attributes
		int totalPages = marketListingsPage.getTotalPages();
		long totalItems = marketListingsPage.getTotalElements();
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);

		// Set model attributes
		model.addAttribute("users", getAllUsers());
		model.addAttribute("allMarketListings", getAllMarketListings());
		model.addAttribute("allWidgets", getAllWidgets());
		model.addAttribute("allSellers", getAllSellers());
		model.addAttribute("allEncodedImages", getAllEncodedImages());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);

		// Set filter attributes
		setFilterAttributes(model, updatedAttributeValues, selectedFilters);

		model.addAttribute("filtered", true);

		return "browseWidgets";
	}

	private void setFilterAttributes(Model model, Map<String, List<String>> updatedAttributeValues,
			Map<String, String> selectedFilters) {
		model.addAttribute("yearValues", updatedAttributeValues.get("Year"));
		model.addAttribute("conditionValues", updatedAttributeValues.get("Condition"));
		model.addAttribute("makeValues", updatedAttributeValues.get("Make"));
		model.addAttribute("modelValues", updatedAttributeValues.get("Model_car"));
		model.addAttribute("keywordValues", updatedAttributeValues.get("Keywords"));
		model.addAttribute("engineValues", updatedAttributeValues.get("Engine"));

		model.addAttribute("year", selectedFilters.get("Year"));
		model.addAttribute("condition", selectedFilters.get("Condition"));
		model.addAttribute("make", selectedFilters.get("Make"));
		model.addAttribute("model_car", selectedFilters.get("Model_car"));
		model.addAttribute("keywords", selectedFilters.get("Keywords"));
		model.addAttribute("engine", selectedFilters.get("Engine"));
	}

	private String encodeImage(Blob imageData) {
		try {
			int blobLength = (int) imageData.length();
			return Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
		} catch (SQLException e) {
			e.printStackTrace();
			return "DIDN'T ENCODE";
		}
	}

	@GetMapping("/BrowseWidgetsButton/page/{pageNumber}/filtered")
	public String filteredBrowsePage(Model model, Principal principal, @PathVariable("pageNumber") int currentPage,
			@RequestParam Map<String, String> allParams) throws UnsupportedEncodingException {
		System.out.println("Browse (With Filter)" + allParams.entrySet());
		User user;
		if (principal == null) {
			user = null;
		} else {
			user = userService.getUserByUsername(principal.getName());
		}

		setPage("searchWidgetSellerMarketListing");
		setMasterPage("query");
		model.addAttribute("masterPage", getMasterPage());
		getAllMarketListings().clear();
		getAllWidgetImages().clear();
		getAllEncodedImages().clear();
		getAllWidgets().clear();
		getAllSellers().clear();
		getAllUsers().clear();

		Iterable<User> allUsersIterator = userController.getAllUsers();
		allUsersIterator.iterator().forEachRemaining(u -> getAllUsers().add(u));

//	    Iterable<MarketListing> allMarketListingsIterator = marketController.getAllListings();
		ArrayList<Object> values = new ArrayList<Object>();

		Page<MarketListing> marketListingsPage = marketListingsPage = marketService.findPageWithFilter(currentPage,
				allParams, values);

		marketListingsPage.iterator().forEachRemaining(u -> getAllMarketListings().add(u));
		marketListingsPage.iterator().forEachRemaining(u -> getAllWidgets().add(u.getWidgetSold()));
		marketListingsPage.iterator().forEachRemaining(u -> getAllSellers().add(u.getSeller()));

		// Get allWidgetImages from marketlistings
		for (MarketListing marketListing : marketListingsPage) {
//	    	List<WidgetImage> images = ;
//	    	WidgetImage[] images = marketListing.getImages().toArray(new WidgetImage[marketListing.getImages().size()]);
			WidgetImage coverImage = widgetImageRepository.findFirstByMarketListing(marketListing);
//	    	System.out.println(coverImage.getId() + " " + coverImage.getImageName());
			Blob imageData = coverImage.getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			allEncodedImages.add(imageString);
		}

		// Encode blob data from listing images and store them in 2d list
//	    for(List<WidgetImage> widgetImageList : allWidgetImages) {
//	    	List<String> listingImagesEncoded = new ArrayList<>();
//	    	for(WidgetImage widgetImage : widgetImageList) {
//	    		Blob imageData = widgetImage.getImageData();
//	    		String imageString;
//	    		int blobLength;
//	    		
//				try {
//					blobLength = (int) imageData.length();
//					imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
//				} catch (SQLException e) {
//					imageString = "DIDN'T ENCODE";
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    		
//	    		listingImagesEncoded.add(imageString);
//	    	}
//	    	allEncodedImages.add(listingImagesEncoded);
//	    }

		int totalPages = marketListingsPage.getTotalPages();
		long totalItems = marketListingsPage.getTotalElements();

		System.out.println("Current Page: " + currentPage + "\nTotal Pages: " + totalPages);

		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);

		model.addAttribute("users", getAllUsers());
		model.addAttribute("allMarketListings", getAllMarketListings());
		model.addAttribute("allWidgets", getAllWidgets());
		model.addAttribute("allSellers", getAllSellers());
		model.addAttribute("allEncodedImages", getAllEncodedImages());
		model.addAttribute("page", getPage());
		model.addAttribute("user", user);
		model.addAttribute("filter", filter);
		model.addAttribute("filtered", true);
		model.addAttribute("filterValues", values);

		return "browseWidgets";
	}

	@RequestMapping({ "", "/", "/index", "/BrowseWidgetsButton" })
	public String getFirstPage(Model model, Principal principal) throws UnsupportedEncodingException {
		User user;
		if (principal == null) {
			user = null;
		} else {
			user = userService.getUserByUsername(principal.getName());
			if (user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_TECHNICALSERVICE")
					|| user.getRole().equals("ROLE_CUSTOMERSERVICE") || user.getRole().equals("ROLE_SALES")
					|| user.getRole().equals("ROLE_HIRINGMANAGER") || user.getRole().equals("ROLE_SECURITY")
					|| user.getRole().equals("ROLE_ADMIN_SHADOW") || user.getRole().equals("ROLE_HELPDESK_ADMIN")
					|| user.getRole().equals("ROLE_HELPDESK_REGULAR")) {
				return "redirect:employee";
			}
		}
		return browseWidgetsButton(model, principal, 1);
	}

	@RequestMapping("/yourListings")
	public String yourListingsButton(Model model, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());

		MarketListing[] userMarketListings = marketController.getListingbyUser(user);
		Iterable<Transaction> soldTransaction = transController.getUserSoldItems(user);
		Iterable<Transaction> purchaseTransaction = transController.getUserPurchases(user);

		List<Long> listingWatchlists = new ArrayList<Long>();

		List<String> encodedImages = new ArrayList<String>();
		List<String> soldImages = new ArrayList<String>();
		List<String> purchaseImages = new ArrayList<String>();

		for (MarketListing marketListing : userMarketListings) {
			listingWatchlists.add(userRepository.countUsersWithMarketListingInWatchlist(marketListing.getId()));
//			System.out.println(userRepository.countUsersWithMarketListingInWatchlist(marketListing.getId()));

			Blob imageData = widgetImageRepository.findByMarketListing(marketListing).get(0).getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			encodedImages.add(imageString);
		}

		for (Transaction transaction : soldTransaction) {

			listingWatchlists
					.add(userRepository.countUsersWithMarketListingInWatchlist(transaction.getMarketListing().getId()));
//			System.out.println(userRepository.countUsersWithMarketListingInWatchlist(transaction.getMarketListing().getId()));

//			System.out.println("MarketID: " + transaction.getMarketListing().getId());
			Blob imageData = widgetImageRepository.findByMarketListing(transaction.getMarketListing()).get(0)
					.getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			soldImages.add(imageString);
		}

		for (Transaction transaction : purchaseTransaction) {

			listingWatchlists
					.add(userRepository.countUsersWithMarketListingInWatchlist(transaction.getMarketListing().getId()));
//			System.out.println(userRepository.countUsersWithMarketListingInWatchlist(transaction.getMarketListing().getId()));

//			System.out.println("MarketID: " + transaction.getMarketListing().getId());
			Blob imageData = widgetImageRepository.findByMarketListing(transaction.getMarketListing()).get(0)
					.getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			purchaseImages.add(imageString);
		}

//		System.out.println(listingViews + "\n" + listingWatchlists);

		page = "yourListings";

		model.addAttribute("marketListings", userMarketListings);
		model.addAttribute("soldTransaction", soldTransaction);
		model.addAttribute("purchaseTransaction", purchaseTransaction);
		model.addAttribute("encodedImages", encodedImages);
		model.addAttribute("soldImages", soldImages);
		model.addAttribute("purchaseImages", purchaseImages);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		model.addAttribute("listingWatchlists", listingWatchlists);

		return "yourListings";
	}

	@RequestMapping("/yourListings/filtered")
	public String yourListingsFiltered(Model model, Principal principal, @RequestParam("filter") String filter) {
		User user = userService.getUserByUsername(principal.getName());

		LocalDate currentDate = LocalDate.now();
		LocalDate cutOffDate;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		switch (filter) {
		case "week":
			cutOffDate = currentDate.minusWeeks(1);
			break;
		case "month":
			cutOffDate = currentDate.minusMonths(1);
			break;
		case "year":
			cutOffDate = currentDate.minusYears(1);
			break;
		case "activeOffers":
			cutOffDate = LocalDate.of(1900, 1, 1); // Set to past date since we don't need date filtering
			break;
		default:
			cutOffDate = currentDate.minusDays(1);
			break;
		}

		MarketListing[] userMarketListings = marketController.getListingbyUser(user);
		Iterable<Transaction> soldTransaction = transController.getUserSoldItems(user);
		Iterable<Transaction> purchaseTransaction = transController.getUserPurchases(user);

		List<Long> listingWatchlists = new ArrayList<>();
		List<MarketListing> userFilteredListings = new ArrayList<>();
		List<Transaction> soldTransactionFiltered = new ArrayList<>();
		List<Transaction> purchaseTransactionFiltered = new ArrayList<>();
		List<String> encodedImages = new ArrayList<>();
		List<String> soldImages = new ArrayList<>();
		List<String> purchaseImages = new ArrayList<>();

		// Process current listings
		for (MarketListing marketListing : userMarketListings) {
			boolean includeInFilter = false;

			if (filter.equals("activeOffers")) {
				// Check if there are any offer notifications for this listing
				List<OfferNotification> offers = offerNotificationRepository
						.findAllByMarketListingId(marketListing.getId());
				includeInFilter = !offers.isEmpty();
			} else {
				LocalDate listingDate = null;
				if (marketListing.getCreationDate() != null) {
					listingDate = marketListing.getCreationDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDate();
					includeInFilter = listingDate.compareTo(cutOffDate) >= 0;
				}
			}

			if (!includeInFilter) {
				continue;
			}

			listingWatchlists.add(userRepository.countUsersWithMarketListingInWatchlist(marketListing.getId()));

			Blob imageData = widgetImageRepository.findByMarketListing(marketListing).get(0).getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				e.printStackTrace();
			}

			userFilteredListings.add(marketListing);
			encodedImages.add(imageString);
		}

		for (Transaction transaction : soldTransaction) {
			LocalDate listingDate = null;
			if (transaction.getMarketListing().getCreationDate() != null) {
				listingDate = transaction.getMarketListing().getCreationDate().toInstant()
						.atZone(ZoneId.systemDefault()).toLocalDate();
				if (listingDate.compareTo(cutOffDate) < 0) {
					continue;
				}
			}

			System.out.println("MarketID: " + transaction.getMarketListing().getId());
			Blob imageData = widgetImageRepository.findByMarketListing(transaction.getMarketListing()).get(0)
					.getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			soldTransactionFiltered.add(transaction);
			soldImages.add(imageString);
		}

		for (Transaction transaction : purchaseTransaction) {
			LocalDate listingDate = null;
			if (transaction.getMarketListing().getCreationDate() != null) {
				listingDate = transaction.getMarketListing().getCreationDate().toInstant()
						.atZone(ZoneId.systemDefault()).toLocalDate();
				if (listingDate.compareTo(cutOffDate) < 0) {
					continue;
				}
			}

			listingWatchlists
					.add(userRepository.countUsersWithMarketListingInWatchlist(transaction.getMarketListing().getId()));

			System.out.println("MarketID: " + transaction.getMarketListing().getId());
			Blob imageData = widgetImageRepository.findByMarketListing(transaction.getMarketListing()).get(0)
					.getImageData();
			String imageString;
			int blobLength;

			try {
				blobLength = (int) imageData.length();
				imageString = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
			} catch (SQLException e) {
				imageString = "DIDN'T ENCODE";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			purchaseTransactionFiltered.add(transaction);
			purchaseImages.add(imageString);
		}

		page = "yourListings";

		String filtered = "filtered";

		model.addAttribute("marketListings", userFilteredListings.toArray());
		model.addAttribute("soldTransaction", soldTransactionFiltered);
		model.addAttribute("purchaseTransaction", purchaseTransactionFiltered);
		model.addAttribute("encodedImages", encodedImages);
		model.addAttribute("soldImages", soldImages);
		model.addAttribute("purchaseImages", purchaseImages);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		model.addAttribute("filtered", filtered);
		model.addAttribute("filter", StringUtils.capitalize(filter));
		model.addAttribute("listingWatchlists", listingWatchlists);

		return "yourListings";
	}

	@RequestMapping("search")
	public String search(@RequestParam("searchString") String searchString, @RequestParam("operator") String operator,
			@RequestParam("price") String price, @RequestParam("category") String category,
			@RequestParam("subCategory") String subCategory, @RequestParam("length") String length,
			@RequestParam("width") String width, @RequestParam("height") String height,
			@RequestParam("capacity") String capacity, @RequestParam("color") String color,
			@RequestParam("condition") String condition, @RequestParam("model") String tempModel,
			@RequestParam("make") String make, @RequestParam("memory") String memory,
			@RequestParam("storage") String storage, @RequestParam("processor") String processor,
			@RequestParam("GPU") String gpu, @RequestParam("developer") String developer,
			@RequestParam("gameTitle") String gameTitle, @RequestParam("console") String console,
			@RequestParam("yardSize") String yardSize, @RequestParam("powerSource") String powerSource,
			@RequestParam("year") String year, @RequestParam("brand") String brand,
			@RequestParam("madeIn") String madeIn, @RequestParam("material") String material,
			@RequestParam("warranty") String warranty, Model model, Model listingModel, Widget tempWidget,
			Model searchModel, MarketListing tempListing, Principal principal) {

		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		listingModel.addAttribute("sellerRating", user.getSellerRating());
		listingModel.addAttribute("user", user);
		List<Widget> widgets = new ArrayList<Widget>();
		List<MarketListing> listings = new ArrayList<MarketListing>();
		List<Widget> allWidgets = new ArrayList<Widget>();
		List<MarketListing> allListings = new ArrayList<MarketListing>();

		allWidgets = StreamSupport.stream(widgetController.getAllWidgets().spliterator(), true)
				.collect(Collectors.toList());
		allListings = StreamSupport.stream(marketController.getAllListings().spliterator(), true)
				.collect(Collectors.toList());

		// TODO:
		// - Loop through and add filter categories from category tree
		// Add filter categories

		model.addAttribute("user", user);
		model.addAttribute("widgets", widgetController.getAllWidgets());
		listingModel.addAttribute("listings", marketController.getAllListings());
		BigDecimal bigPrice = new BigDecimal("0.00");
		try {
			System.out.println("item: " + searchString);
		} catch (Exception e) {

		}
		try {
			System.out.println(operator);
		} catch (Exception e) {

		}
		try {
			bigPrice = BigDecimal.valueOf(Double.valueOf(price));
		} catch (Exception e) {

		}

		if (category.contentEquals("all")) {
			if (searchString.isEmpty() && price.isEmpty()) {

				widgets = this.onlyCategory(tempWidget, tempListing, category, widgets, allWidgets, allListings);

				try {
					searchModel.addAttribute("searchWidgets", widgets);
				} catch (Exception e) {
					System.out.println("No widgets");
				}
			}
			if (!searchString.isEmpty() && price.isEmpty()) {

				widgets = this.stringOnly(tempWidget, widgets, tempListing, searchString, category, allWidgets,
						allListings);

				try {
					searchModel.addAttribute("searchWidgets", widgets);
				} catch (Exception e) {
					System.out.println("No widgets");
				}

			} else if (searchString.isEmpty() && !price.isEmpty()) {
				System.out.println("only price");
				model.addAttribute("operator", operator);
				model.addAttribute("price", price);
				model.addAttribute("widgets", widgetController.getAllWidgets());
				listingModel.addAttribute("listings", marketController.getAllListings());

				widgets = this.priceOnly(tempWidget, widgets, tempListing, searchString, operator, bigPrice, category,
						allWidgets, allListings);
				searchModel.addAttribute("searchWidgets", widgets);
			} else if (!searchString.isEmpty() && !price.isEmpty()) {
				System.out.println("both");
				model.addAttribute("searchString", searchString);
				model.addAttribute("operator", operator);
				model.addAttribute("price", price);
				model.addAttribute("widgets", widgetController.getAllWidgets());
				listingModel.addAttribute("listings", marketController.getAllListings());

				widgets = this.stringAndPrice(tempWidget, widgets, tempListing, searchString, operator, bigPrice,
						category, allWidgets, allListings);
				searchModel.addAttribute("searchWidgets", widgets);
			}
		}

		// TODO:
		// - Add filters back

		if (searchString.isEmpty() && price.isEmpty()) {
			System.out.println("only category");
			model.addAttribute("widgets", widgetController.getAllWidgets());
			listingModel.addAttribute("listings", marketController.getAllListings());
			widgets = this.onlyCategory(tempWidget, tempListing, category, widgets, allWidgets, allListings);
			try {
				searchModel.addAttribute("searchWidgets", widgets);
			} catch (Exception e) {
				System.out.println("No widgets");
			}
		}
		if (!searchString.isEmpty() && price.isEmpty()) {
			System.out.println("only search string");
			model.addAttribute("searchString", searchString);
			model.addAttribute("widgets", widgetController.getAllWidgets());
			listingModel.addAttribute("listings", marketController.getAllListings());
			widgets = this.stringOnly(tempWidget, widgets, tempListing, searchString, category, allWidgets,
					allListings);
			searchModel.addAttribute("searchWidgets", widgets);
		} else if (searchString.isEmpty() && !price.isEmpty()) {
			System.out.println("only price");
			model.addAttribute("operator", operator);
			model.addAttribute("price", price);
			model.addAttribute("widgets", widgetController.getAllWidgets());
			listingModel.addAttribute("listings", marketController.getAllListings());
			widgets = this.priceOnly(tempWidget, widgets, tempListing, searchString, operator, bigPrice, category,
					allWidgets, allListings);
			searchModel.addAttribute("searchWidgets", widgets);
		} else if (!searchString.isEmpty() && !price.isEmpty()) {
			System.out.println("both");
			model.addAttribute("searchString", searchString);
			model.addAttribute("operator", operator);
			model.addAttribute("price", price);
			model.addAttribute("widgets", widgetController.getAllWidgets());
			listingModel.addAttribute("listings", marketController.getAllListings());
			widgets = this.stringAndPrice(tempWidget, widgets, tempListing, searchString, operator, bigPrice, category,
					allWidgets, allListings);
			searchModel.addAttribute("searchWidgets", widgets);
		}

		System.out.println(widgets.size());
		return "searchResult";
	}

	public List<Widget> onlyCategory(Widget tempWidget, MarketListing tempListing, String category,
			List<Widget> widgets, List<Widget> allWidgets, List<MarketListing> allListings) {
		int id = 0;
		ArrayList<Long> allIds = new ArrayList<Long>();
		int marker = 1;
		if (!allWidgets.isEmpty()) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();
		}
		System.out.println(tempWidget.getCategory());
		while (tempWidget != null) {
			if ((tempListing != null && tempListing.getQtyAvailable() > 0 && !tempListing.isDeleted()
					&& tempWidget.getCategory().getName().contentEquals(category))
					|| (tempListing != null && !tempListing.isDeleted() && category.contentEquals("all")
							&& tempListing.getQtyAvailable() > 0)) {
				if (!allIds.isEmpty()) {
					if (allIds.contains(tempWidget.getId())) {
						marker = 0;
					}
				}
				if (marker != 0) {
					widgets.add(tempWidget);
					allIds.add(tempWidget.getId());
				}
			}

			id++;
			try {
				tempListing = allListings.get(id);
				tempWidget = tempListing.getWidgetSold();
			} catch (Exception e) {
				break;
			}
		}
		return widgets;
	}

	public List<Widget> stringOnly(Widget tempWidget, List<Widget> widgets, MarketListing tempListing,
			String searchString, String category, List<Widget> allWidgets, List<MarketListing> allListings) {
		int id = 0;
		int marker = 1;
		ArrayList<Long> allIds = new ArrayList<Long>();
		if (!allWidgets.isEmpty()) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();
		}
		while (tempWidget != null) {
			if ((tempWidget.getName() != null && (tempWidget.getName().contains(searchString)
					|| tempWidget.getName().toUpperCase().contains(searchString)
					|| tempWidget.getName().toLowerCase().contains(searchString)
					|| tempWidget.getDescription().contains(searchString)
					|| tempWidget.getDescription().toUpperCase().contains(searchString)
					|| tempWidget.getDescription().toLowerCase().contains(searchString)))
					&& (tempWidget.getCategory().getName().contentEquals(category)) && (category.contentEquals("all"))
					&& tempListing.getQtyAvailable() > 0) {
				if (tempListing != null && !tempListing.isDeleted()) {
					if (!allIds.isEmpty()) {
						if (allIds.contains(tempWidget.getId())) {
							marker = 0;
						}
					}
					if (marker != 0) {
						widgets.add(tempWidget);
						allIds.add(tempWidget.getId());
					}
				}
			}
			id++;
			try {
				tempListing = allListings.get(id);
				tempWidget = tempListing.getWidgetSold();
			} catch (Exception e) {
				break;
			}
			System.out.println(tempWidget.getId());
		}

		return widgets;
	}

	public List<Widget> priceOnly(Widget tempWidget, List<Widget> widgets, MarketListing tempListing,
			String searchString, String operator, BigDecimal bigPrice, String category, List<Widget> allWidgets,
			List<MarketListing> allListings) {
		int id = 0;
		int marker = 1;
		ArrayList<Long> allIds = new ArrayList<Long>();
		if (!allWidgets.isEmpty()) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();
		}

		while (tempWidget != null) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();

			int res = tempListing.getPricePerItem().compareTo(bigPrice);
			if (operator.contentEquals("greater") && res == 1
					&& (tempWidget.getCategory().getName().contentEquals(category) || category.contentEquals("all"))
					&& tempListing.getQtyAvailable() > 0) {
				if (!tempListing.isDeleted()) {
					if (!allIds.isEmpty()) {
						if (allIds.contains(tempWidget.getId())) {
							marker = 0;
						}
					}
					if (marker != 0) {
						widgets.add(tempWidget);
						allIds.add(tempWidget.getId());
					}
				}
			} else if (operator.contentEquals("lesser") && res == -1
					&& (tempWidget.getCategory().getName().contentEquals(category) || category.contentEquals("all"))
					&& tempListing.getQtyAvailable() > 0) {
				if (!tempListing.isDeleted()) {
					if (!allIds.isEmpty()) {
						if (allIds.contains(tempWidget.getId())) {
							marker = 0;
						}
					}
					if (marker != 0) {
						widgets.add(tempWidget);
						allIds.add(tempWidget.getId());
					}
				}
			}
			id++;
			try {
				tempListing = allListings.get(id);
				tempWidget = tempListing.getWidgetSold();
			} catch (Exception e) {
				break;
			}
		}
		return widgets;
	}

	public List<Widget> stringAndPrice(Widget tempWidget, List<Widget> widgets, MarketListing tempListing,
			String searchString, String operator, BigDecimal bigPrice, String category, List<Widget> allWidgets,
			List<MarketListing> allListings) {
		int id = 0;
		int marker = 1;
		ArrayList<Long> allIds = new ArrayList<Long>();
		if (!allWidgets.isEmpty()) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();
		}

		while (tempWidget != null) {
			tempListing = allListings.get(id);
			tempWidget = tempListing.getWidgetSold();
			if ((tempWidget.getName().contains(searchString)
					|| tempWidget.getName().toUpperCase().contains(searchString)
					|| tempWidget.getName().toLowerCase().contains(searchString)
					|| tempWidget.getDescription().contains(searchString)
					|| tempWidget.getDescription().toUpperCase().contains(searchString)
					|| tempWidget.getDescription().toLowerCase().contains(searchString))
					&& (tempWidget.getCategory().getName().contentEquals(category) || category.contentEquals("all"))
					&& tempListing.getQtyAvailable() > 0) {
				int res = tempListing.getPricePerItem().compareTo(bigPrice);
				if (operator.contentEquals("greater") && (res == 1)) {
					if (!tempListing.isDeleted()) {
						if (!allIds.isEmpty()) {
							if (allIds.contains(tempWidget.getId())) {
								marker = 0;
							}
						}
						if (marker != 0) {
							widgets.add(tempWidget);
							allIds.add(tempWidget.getId());
						}
					}
				} else if (operator.contentEquals("lesser") && (res == -1)) {
					if (!tempListing.isDeleted()) {
						if (!allIds.isEmpty()) {
							if (allIds.contains(tempWidget.getId())) {
								marker = 0;
							}
						}
						if (marker != 0) {
							widgets.add(tempWidget);
							allIds.add(tempWidget.getId());
						}
					}
				}
			}
			id++;
			try {
				tempListing = allListings.get(id);
				tempWidget = tempListing.getWidgetSold();
			} catch (Exception e) {
				break;
			}
		}
		return widgets;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<MarketListing> getAllMarketListings() {
		return allMarketListings;
	}

	public void setAllMarketListings(List<MarketListing> allMarketListings) {
		this.allMarketListings = allMarketListings;
	}

	public List<Widget> getAllWidgets() {
		return allWidgets;
	}

	public void setAllWidgets(List<Widget> allWidgets) {
		this.allWidgets = allWidgets;
	}

	public List<List<WidgetImage>> getAllWidgetImages() {
		return allWidgetImages;
	}

	public void setAllWidgetImages(List<List<WidgetImage>> allWidgetImages) {
		this.allWidgetImages = allWidgetImages;
	}

//  public List<List<String>> getAllEncodedImages() {
//	return allEncodedImages;
//}
	public List<String> getAllEncodedImages() {
		return allEncodedImages;
	}

	public void setAllEncodedImages(List<String> allEncodedImages) {
		this.allEncodedImages = allEncodedImages;
	}

//public void setAllEncodedImages(List<List<String>> allEncodedImages) {
//	this.allEncodedImages = allEncodedImages;
//}

	public List<User> getAllSellers() {
		return allSellers;
	}

	public void setAllSellers(List<User> allSellers) {
		this.allSellers = allSellers;
	}

	public List<Message> getAllMessages() {
		return allMessages;
	}

	public void setAllMessages(List<Message> allMessages) {
		this.allMessages = allMessages;
	}

	public String getPage2() {
		return page2;
	}

	public void setPage2(String page2) {
		this.page2 = page2;
	}

	public String getPage3() {
		return page3;
	}

	public void setPage3(String page3) {
		this.page3 = page3;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public String getMasterPage() {
		return masterPage;
	}

	public void setMasterPage(String masterPage) {
		this.masterPage = masterPage;
	}

	@GetMapping("/BrowseWidgetsButton/page/{pageNumber}/search")
	public String searchWidgets(@PathVariable("pageNumber") int currentPage,
			@RequestParam(required = false) String searchTerm, Model model, Principal principal)
			throws UnsupportedEncodingException {
		User user = (principal != null) ? userService.getUserByUsername(principal.getName()) : null;

		if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			Page<MarketListing> searchResults = marketListingRepository.searchMarketListings(searchTerm.trim(),
					PageRequest.of(currentPage - 1, 4));

			getAllMarketListings().clear();
			searchResults.getContent().forEach(listing -> getAllMarketListings().add(listing));

			processSearchResults(searchResults, model);

			model.addAttribute("searchTerm", searchTerm);
		} else {
			return browseWidgetsButton(model, principal, currentPage);
		}

		model.addAttribute("currentPage", currentPage);
		model.addAttribute("user", user);
		model.addAttribute("page", "searchWidgetSellerMarketListing");

		return "browseWidgets";
	}

	private void processSearchResults(Page<MarketListing> searchResults, Model model) {
		getAllWidgets().clear();
		getAllSellers().clear();
		getAllEncodedImages().clear();

		for (MarketListing listing : searchResults.getContent()) {
			getAllWidgets().add(listing.getWidgetSold());
			getAllSellers().add(listing.getSeller());

			try {
				WidgetImage coverImage = widgetImageRepository.findFirstByMarketListing(listing);
				if (coverImage != null && coverImage.getImageData() != null) {
					Blob imageData = coverImage.getImageData();
					int blobLength = (int) imageData.length();
					String encodedImage = Base64.getEncoder().encodeToString(imageData.getBytes(1, blobLength));
					getAllEncodedImages().add(encodedImage);
				} else {
					getAllEncodedImages().add(null);
				}
			} catch (SQLException e) {
				log.error("Error processing image for listing {}: {}", listing.getId(), e.getMessage());
				getAllEncodedImages().add(null);
			}
		}

		model.addAttribute("allMarketListings", getAllMarketListings());
		model.addAttribute("allWidgets", getAllWidgets());
		model.addAttribute("allSellers", getAllSellers());
		model.addAttribute("allEncodedImages", getAllEncodedImages());
		model.addAttribute("totalPages", searchResults.getTotalPages());
		model.addAttribute("totalItems", searchResults.getTotalElements());
	}

}