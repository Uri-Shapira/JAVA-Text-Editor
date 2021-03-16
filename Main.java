import java.io.Serializable;
import java.util.List;

public class Main {

    public static void main(String[] args){
        WordEditor editor = new WordEditor();
        editor.add("test test");
        editor.add(" another value");
        editor.print();
        editor.undo();
        editor.print();
        editor.redo();
        editor.underline(2,3);
        editor.add(" BLA ",4);
        editor.print();
        editor.underline(0,4);
        editor.bold(1,2);
        editor.remove(6,8);
        editor.print();
    }


}
