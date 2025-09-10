package edu.sru.cpsc.webshopping;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.annotation.EnableScheduling;

import edu.sru.cpsc.webshopping.service.SuggestionService;
import edu.sru.cpsc.webshopping.service.TaxExcelToDatabaseService;
import edu.sru.cpsc.webshopping.util.PreLoad;
import jakarta.servlet.ServletContext;

@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SellingWidgets extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private PreLoad preLoad;

    @Autowired
    private Environment env;

    @Autowired
    private TaxExcelToDatabaseService taxExcelToDatabaseService;

    @Value("${server.port}")
    private String port;
    
    @Value("${spring.datasource.username}")
    private String userName;
    
    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private SuggestionService suggestionService;

    public SellingWidgets() {
    }

    public static void main(String[] args) {
        SpringApplication.run(SellingWidgets.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws SQLException, IOException {
        Resource sqlResource = new ClassPathResource("import.sql");
        Resource[] defaultImages = {
            new ClassPathResource("static/listingImages/71corvetesbautokit.jpg"), 
            new ClassPathResource("static/listingImages/hummer h3.jpg"),
            new ClassPathResource("static/listingImages/2020-chevrolet-corvette-lt2-engine-1563399415.jpg"),
            new ClassPathResource("static/listingImages/MT82-Gen-3-1__25921.jpg"),
            new ClassPathResource("static/listingImages/s-l400.jpg"),
            new ClassPathResource("static/listingImages/electric_kettle.jpg"),
            new ClassPathResource("static/listingImages/leather_jacket.jpg"),
            new ClassPathResource("static/listingImages/running_shoes.jpg"),
            new ClassPathResource("static/listingImages/yoga_mat.jpg"),
            new ClassPathResource("static/listingImages/tshirt.jpg"),
            new ClassPathResource("static/listingImages/coffee_maker.jpg"),
            new ClassPathResource("static/listingImages/lawn_mower.jpg"),
            new ClassPathResource("static/listingImages/basketball.jpg"),
            new ClassPathResource("static/listingImages/canvas_painting.jpg"),
            new ClassPathResource("static/listingImages/lipstick.jpg"),
            new ClassPathResource("static/listingImages/sunglasses.jpg"),
            new ClassPathResource("static/listingImages/blender.jpg"),
            new ClassPathResource("static/listingImages/soccer_ball.jpg"),
            new ClassPathResource("static/listingImages/makeup_kit.jpg"),
            new ClassPathResource("static/listingImages/art_supplies.jpg"),
            new ClassPathResource("static/listingImages/automobile_battery.jpg"),
            new ClassPathResource("static/listingImages/car_wash_kit.jpg"),
            new ClassPathResource("static/listingImages/home_security_system.jpg"),
            new ClassPathResource("static/listingImages/winter_jacket.jpg"),
            new ClassPathResource("static/listingImages/art-book.jpg"),
            new ClassPathResource("static/listingImages/perfume.jpg")
        };

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/SellingWidgets", userName, password);
            connection.setAutoCommit(false);

            try {
                ScriptUtils.executeSqlScript(connection, new EncodedResource(sqlResource), true, true, "--", ";", "/*", "*/");
            } catch (Exception e) {
                System.out.println("Issue with import.sql: " + e.getMessage());
            }

            // Update images in batches
            String updateQuery = "UPDATE widget_image SET image_data = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                for (int i = 0; i < defaultImages.length; i++) {
                    Blob img = BlobProxy.generateProxy(defaultImages[i].getContentAsByteArray());
                    statement.setBlob(1, img);
                    statement.setInt(2, i + 1);
                    statement.addBatch();

                    if ((i + 1) % 10 == 0 || i == defaultImages.length - 1) {
                        statement.executeBatch();
                        connection.commit();
                    }
                }
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error updating images: " + e.getMessage());
            }

            // Load suggestions
            Resource suggestions = new ClassPathResource("Website_Car_Parts_Categories.xlsx");
            try {
                suggestionService.loadSuggestions(suggestions);
            } catch (Exception e) {
                System.out.println("Error loading suggestions: " + e.getMessage());
            }

        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
//      BackupService backupService = BackupService.getInstance();
//      backupService.scheduleBackup(new Date(System.currentTimeMillis()), "Daily");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SellingWidgets.class);
    }

    @Override
    public void run(String... args) throws Exception {
        String ddlAuto = env.getProperty("spring.jpa.hibernate.ddl-auto");
        if (!"update".equals(ddlAuto)) {
            //preLoad.importCategoriesFromCSV();
        }
      //get ip address
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("\nRunning on https://" + ip + ":" + port + servletContext.getContextPath() + "\n");
    }
}