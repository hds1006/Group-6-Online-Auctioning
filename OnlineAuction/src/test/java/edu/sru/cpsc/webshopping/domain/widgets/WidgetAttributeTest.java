package edu.sru.cpsc.webshopping.domain.widgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WidgetAttributeTest {

    private WidgetAttribute widgetAttribute;
    private Attribute attribute;
    private Widget widget;

    @BeforeEach
    public void setUp() {
        attribute = new Attribute();
        attribute.setAttributeKey("color");
        widget = new Widget();
        widget.setName("Widget 1");
        widgetAttribute = new WidgetAttribute(widget, attribute);
    }

    @Test
    public void testGettersAndSetters() {
        // Test initial values
        assertEquals(0, widgetAttribute.getId());
        assertEquals("color", widgetAttribute.getAttributeKey());
        assertEquals(attribute, widgetAttribute.getAttribute());
        assertNull(widgetAttribute.getValue());
        assertEquals(widget, widgetAttribute.getWidget());

        // Test ID setter/getter
        widgetAttribute.setId(1);
        assertEquals(1, widgetAttribute.getId());

        // Test attributeKey setter/getter
        widgetAttribute.setAttributeKey("size");
        assertEquals("size", widgetAttribute.getAttributeKey());

        // Test attribute setter/getter with key update
        Attribute newAttribute = new Attribute();
        newAttribute.setAttributeKey("weight");
        widgetAttribute.setAttribute(newAttribute);
        assertEquals(newAttribute, widgetAttribute.getAttribute());
        assertEquals("weight", widgetAttribute.getAttributeKey()); // Verify key is updated with setAttribute

        // Test value setter/getter
        widgetAttribute.setValue("large");
        assertEquals("large", widgetAttribute.getValue());

        // Test widget setter/getter
        Widget newWidget = new Widget();
        newWidget.setName("Widget 2");
        widgetAttribute.setWidget(newWidget);
        assertEquals(newWidget, widgetAttribute.getWidget());
    }

    @Test
    public void testConstructors() {
        // Test default constructor
        WidgetAttribute widgetAttribute1 = new WidgetAttribute();
        assertNotNull(widgetAttribute1);
        
        // Test constructor with attribute only
        WidgetAttribute widgetAttribute2 = new WidgetAttribute(attribute);
        assertEquals("color", widgetAttribute2.getAttributeKey());
        assertEquals(attribute, widgetAttribute2.getAttribute());
        assertNull(widgetAttribute2.getWidget()); // Verify widget is null
        
        // Test constructor with widget and attribute
        WidgetAttribute widgetAttribute3 = new WidgetAttribute(widget, attribute);
        assertEquals("color", widgetAttribute3.getAttributeKey());
        assertEquals(attribute, widgetAttribute3.getAttribute());
        assertEquals(widget, widgetAttribute3.getWidget());
    }

    @Test
    public void testAttributeKeySync() {
        // Test that attributeKey stays in sync when attribute changes
        Attribute newAttribute = new Attribute();
        newAttribute.setAttributeKey("newKey");
        widgetAttribute.setAttribute(newAttribute);
        assertEquals("newKey", widgetAttribute.getAttributeKey());
        
        // Test direct attributeKey change
        widgetAttribute.setAttributeKey("directChange");
        assertEquals("directChange", widgetAttribute.getAttributeKey());
    }
}