package edu.sru.cpsc.webshopping.domain.widgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WidgetTest.class})
public class WidgetTest {

    private Widget widget;
    private Category category;
    private WidgetAttribute widgetAttribute;

    @BeforeEach
    void setUp() {
        widget = new Widget();
        category = new Category("category");
        widgetAttribute = new WidgetAttribute(); // Assuming WidgetAttribute has an appropriate constructor or setters
        
        Set<WidgetAttribute> widgetAttributes = new HashSet<>();
        widgetAttributes.add(widgetAttribute);

        widget.setCategory(category);
        widget.setDescription("description");
        widget.setId(21);
        widget.setName("test");
        widget.setAttributes(widgetAttributes);
    }

    /*
     * Tests that basic widget information and associations are correctly stored and retrieved
     */
    @Test
    void testWidgetAttributes() {
        assertEquals("category", widget.getCategory().getName());
        assertEquals("description", widget.getDescription());
        assertEquals(21, widget.getId());
        assertEquals("test", widget.getName());
        assertTrue(widget.getAttributes().contains(widgetAttribute), "Widget attributes should contain the specified widgetAttribute");
    }

    /*
     * Tests the new getWidgetAttributes method
     */
    @Test
    void testGetWidgetAttributes() {
        Set<WidgetAttribute> attributes = widget.getWidgetAttributes();
        assertNotNull(attributes, "Widget attributes should not be null");
        assertEquals(1, attributes.size(), "Widget should have exactly one attribute");
        assertTrue(attributes.contains(widgetAttribute), "Widget attributes should contain the specified widgetAttribute");
        assertEquals(widget.getAttributes(), attributes, "getAttributes and getWidgetAttributes should return the same set");
    }

    /*
     * Tests widget creation with null attributes
     */
    @Test
    void testWidgetWithNullAttributes() {
        Widget newWidget = new Widget();
        newWidget.setCategory(category);
        newWidget.setDescription("test description");
        newWidget.setId(22);
        newWidget.setName("test widget");
        
        assertNotNull(newWidget, "Widget should be created successfully");
        assertEquals("test description", newWidget.getDescription());
        assertEquals("test widget", newWidget.getName());
        assertEquals(22, newWidget.getId());
    }
}