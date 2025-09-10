package edu.sru.cpsc.webshopping.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {StatisticsTest.class})
public class StatisticsTest {
    
    private Statistics statistics;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        statistics = new Statistics();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(statistics, "Statistics object should be created successfully");
    }

    @Test
    void testParameterizedConstructor() {
        Statistics.StatsCategory category = Statistics.StatsCategory.AUCTION;
        float value = 100.0f;
        Statistics stats = new Statistics(category, value);
        
        assertNotNull(stats.getId(), "ID should not be null");
        assertEquals(category, stats.getCategory(), "Category should match the constructor parameter");
        assertEquals(value, stats.getValue(), "Value should match the constructor parameter");
        assertNotNull(stats.getDate(), "Date should be automatically set");
        assertEquals(LocalDateTime.now().getHour(), stats.getHour(), "Hour should match current hour");
    }

    @Test
    void testSettersAndGetters() {
        // Test ID
        statistics.setId(1L);
        assertEquals(1L, statistics.getId(), "ID should be set and retrieved correctly");

        // Test Value
        float testValue = 100.0f;
        statistics.setValue(testValue);
        assertEquals(testValue, statistics.getValue(), "Value should be set and retrieved correctly");

        // Test Date
        statistics.setDate(testDate);
        assertEquals(testDate, statistics.getDate(), "Date should be set and retrieved correctly");

        // Test Hour
        statistics.setDate(testDate);
        statistics.setHour();
        assertEquals(testDate.getHour(), statistics.getHour(), "Hour should be set from date correctly");

        // Test Description
        String testDescription = "Test Description";
        statistics.setDescription(testDescription);
        assertEquals(testDescription, statistics.getDescription(), "Description should be set and retrieved correctly");
    }

    @Test
    void testStatsCategoryEnum() {
        // Test all enum values are accessible
        assertTrue(Statistics.StatsCategory.values().length > 0, "StatsCategory should have defined values");
        
        // Test specific enum values
        assertEquals("AUCTION", Statistics.StatsCategory.AUCTION.toString(), "Enum toString should match name");
        assertEquals("SALEVALUE", Statistics.StatsCategory.SALEVALUE.toString(), "Enum toString should match name");
        assertEquals("SALE", Statistics.StatsCategory.SALE.toString(), "Enum toString should match name");
        
        // Test category getter
        Statistics.StatsCategory testCategory = Statistics.StatsCategory.AUCTION;
        Statistics stats = new Statistics(testCategory, 100.0f);
        assertEquals(testCategory, stats.getCategory(), "Category should be retrievable via getter");
    }

    @Test
    void testDateTimeHandling() {
        LocalDateTime now = LocalDateTime.now();
        statistics.setDate(now);
        
        assertEquals(now, statistics.getDate(), "Date should be set and retrieved correctly");
        statistics.setHour();
        assertEquals(now.getHour(), statistics.getHour(), "Hour should be extracted from date correctly");
    }
    
    @Test
    void testAllStatsCategoryValues() {
        // Verify all enum values can be used to create valid Statistics objects
        for (Statistics.StatsCategory category : Statistics.StatsCategory.values()) {
            Statistics stats = new Statistics(category, 1.0f);
            assertEquals(category, stats.getCategory(), 
                String.format("Statistics should handle %s category correctly", category.name()));
            assertNotNull(stats.getDate(), 
                String.format("Date should be set for %s category", category.name()));
        }
    }
}