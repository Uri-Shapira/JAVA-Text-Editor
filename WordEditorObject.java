import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This object contains the contents of the current word editor
 * currentText - the current text in the editor
 * undoStack - stack containing all operations that can be undone
 * redoStack - stack containing all operation that have been undone and can be re-done
 */
public class WordEditorObject implements Serializable {

    private final Stack<WordEditorOperation> undoStack;
    private final Stack<WordEditorOperation> redoStack;
    private final List<WordEditorCharacter> currentText;

    public WordEditorObject(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        currentText = new ArrayList<>();
    }

    public Stack<WordEditorOperation> getRedoStack() {
        return redoStack;
    }

    public Stack<WordEditorOperation> getUndoStack() {
        return undoStack;
    }

    public List<WordEditorCharacter> getCurrentText() {
        return currentText;
    }

}
