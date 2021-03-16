import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object defining an operation of the wordEditor
 * Available operations are - add, remove, bold, underline, italic
 * operationParameters holds all relevant parameters for a given operation
 */
public class WordEditorOperation implements Serializable {
    private final String operationName;
    private final Map<String,String> operationParameters;

    public WordEditorOperation(String word){
        this.operationName = "add";
        operationParameters = new HashMap<>();
        operationParameters.put("word", word);
    }

    public WordEditorOperation(String word, int position){
        this.operationName = "add";
        operationParameters = new HashMap<>();
        operationParameters.put("word", word);
        operationParameters.put("position", position + "");
    }

    public WordEditorOperation(String operationName, int fromPosition, int toPosition){
        this.operationName = operationName;
        operationParameters = new HashMap<>();
        operationParameters.put("fromPosition", fromPosition + "");
        operationParameters.put("toPosition", toPosition + "");
    }

    public WordEditorOperation(String operationName, int fromPosition, int toPosition, String word){
        this.operationName = operationName;
        operationParameters = new HashMap<>();
        operationParameters.put("fromPosition", fromPosition + "");
        operationParameters.put("toPosition", toPosition + "");
        operationParameters.put("word", word);
    }

    public String getOperationName() {
        return operationName;
    }

    public Map<String,String> getOperationParameters(){
        return operationParameters;
    }

}
