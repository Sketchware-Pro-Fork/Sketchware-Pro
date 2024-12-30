package pro.sketchware.activities.editor.theme;

public class ThemeItem {
    private String colorName;
    private String attributeName;
    private String colorValue;
    private String attributeValue;
    
    public ThemeItem(final String colorName, final String attributeName, final String colorValue, final String attributeValue) {
        this.colorName = colorName;
        this.attributeName = attributeName;
        this.colorValue = colorValue;
        this.attributeValue = attributeValue;
    }
    
    public void setColorName(final String colorName) {
        this.colorName = colorName;
    }
    
    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }
    
    public void setColorValue(final String colorValue) {
        this.colorValue = colorValue;
    }
    
    public void setAttributeValue(final String attributeValue) {
        this.attributeValue = attributeValue;
    }
    
    public String getColorName() {
        return colorName;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    
    public String getColorValue() {
        return colorValue;
    }
    
    public String getAttributeValue() {
        return attributeValue;
    }
}