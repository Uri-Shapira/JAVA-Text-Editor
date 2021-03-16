import java.io.Serializable;

/**
 * Object defining a character with the currentText of the wordEditor
 * This character can be any combination of bold, underlined, and italics.
 */
public class WordEditorCharacter implements Serializable {

    private final Character c;
    private boolean isBold;
    private boolean isUnderlined;
    private boolean isItalics;

    public WordEditorCharacter(Character c){
        this.c = c;
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isItalics() {
        return isItalics;
    }

    public boolean isUnderlined() {
        return isUnderlined;
    }

    public Character getC(){
        return c;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public void setItalics(boolean italics) {
        isItalics = italics;
    }

    public void setUnderlined(boolean underlined) {
        isUnderlined = underlined;
    }

}
