import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The main object of the application.
 * Has a wordEditorObject object which contains the current text and the history.
 * The information is saved after every operation to a file with name metadataFileName
 */

public class WordEditor {

    private WordEditorObject wordEditorObject;
    private String metadataFileName;

    public WordEditor() {
        getOrCreateMetadataFile();
    }

    public WordEditorObject getWordEditorObject() {
        return wordEditorObject;
    }

    /**
     * If the metadata file already exists, retrieves the wordEditorObject
     * otherwise, a new file and object is created (in case this is the first time the user opens the word editor)
     */
    private void getOrCreateMetadataFile() {
        metadataFileName = "editor_metadata";
        Path path = Paths.get(metadataFileName);
        boolean fileExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS });
        if(fileExists){
            try{
                FileInputStream fileInputStream = new FileInputStream(metadataFileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                wordEditorObject = (WordEditorObject) objectInputStream.readObject();
            }
            catch(Exception e){
                System.out.println("Failed to read object from file. Error: " + e.getMessage());
                System.exit(1);
            }

        }
        else{
            wordEditorObject = new WordEditorObject();
            File file = new File(metadataFileName);
            try{
                file.createNewFile();
            }
            catch(Exception e){
                System.out.println("Failed to create metadata file. Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    /**
     * Writes the serialized wordEditorObject to the metadata file
     */
    private void writeObjectToMetadataFile(){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(new File(metadataFileName));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(wordEditorObject);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch(Exception e){

        }

    }

    public void add(String s){
        for(int i = 0; i < s.length(); i++){
            wordEditorObject.getCurrentText().add(new WordEditorCharacter(s.charAt(i)));
        }
        WordEditorOperation operation = new WordEditorOperation(s);
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    public void add(String s, int position){
        WordEditorOperation operation = new WordEditorOperation(s, position);
        for(int i = 0; i < s.length(); i++){
            wordEditorObject.getCurrentText().add(position, new WordEditorCharacter(s.charAt(i)));
            position += 1;
        }
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    public void remove(int fromPosition, int toPosition){
        int counter = toPosition - fromPosition + 1;
        StringBuilder wordToRemove = new StringBuilder();
        while(counter > 0){
            wordToRemove.append(wordEditorObject.getCurrentText().get(fromPosition).getC());
            wordEditorObject.getCurrentText().remove(fromPosition);
            counter -= 1;
        }
        WordEditorOperation operation = new WordEditorOperation("remove", fromPosition, toPosition, wordToRemove.toString());
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    public void italic(int fromPosition, int toPosition){
        for(int i = fromPosition; i <= toPosition; i++){
            wordEditorObject.getCurrentText().get(i).setItalics(true);
        }
        WordEditorOperation operation = new WordEditorOperation("italic", fromPosition, toPosition);
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    public void bold(int fromPosition, int toPosition){
        for(int i = fromPosition; i <= toPosition; i++){
            wordEditorObject.getCurrentText().get(i).setBold(true);
        }
        WordEditorOperation operation = new WordEditorOperation("bold", fromPosition, toPosition);
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    public void underline(int fromPosition, int toPosition){
        for(int i = fromPosition; i <= toPosition; i++){
            wordEditorObject.getCurrentText().get(i).setUnderlined(true);
        }
        WordEditorOperation operation = new WordEditorOperation("underline", fromPosition, toPosition);
        wordEditorObject.getUndoStack().push(operation);
        writeObjectToMetadataFile();
    }

    /**
     * Undo a formatting operation - bold, underline, or italic
     * @param operationToUndo
     */
    private void undoStringFormatting(WordEditorOperation operationToUndo){
        int fromIndex = Integer.parseInt(operationToUndo.getOperationParameters().get("fromPosition"));
        int toIndex = Integer.parseInt(operationToUndo.getOperationParameters().get("toPosition"));
        if(operationToUndo.getOperationName().equals("bold")){
            for(int i = fromIndex; i <= toIndex; i++) {
                wordEditorObject.getCurrentText().get(i).setBold(false);
            }
        }
        else if(operationToUndo.getOperationName().equals("underline")){
            for(int i = fromIndex; i <= toIndex; i++) {
                wordEditorObject.getCurrentText().get(i).setUnderlined(false);
            }
        }
        else{
            for(int i = fromIndex; i <= toIndex; i++) {
                wordEditorObject.getCurrentText().get(i).setItalics(false);
            }
        }
    }

    private void undoAddOperation(WordEditorOperation operationToUndo){
        String word = operationToUndo.getOperationParameters().get("word");
        int indexToDelete;
        int deleteCounter = word.length();
        if(operationToUndo.getOperationParameters().containsKey("position")) {
            indexToDelete = Integer.parseInt(operationToUndo.getOperationParameters().get("position"));
            while (deleteCounter > 0) {
                wordEditorObject.getCurrentText().remove(indexToDelete);
                deleteCounter -= 1;
            }
        }
        else{
            while(deleteCounter > 0){
                indexToDelete = wordEditorObject.getCurrentText().size() - 1;
                wordEditorObject.getCurrentText().remove(indexToDelete);
                deleteCounter -= 1;
            }
        }
    }

    private void undoRemoveOperation(WordEditorOperation operationToUndo){
        String wordRemoved = operationToUndo.getOperationParameters().get("word");
        int indexToAdd = Integer.parseInt(operationToUndo.getOperationParameters().get("fromPosition"));
        for(int i = 0; i < wordRemoved.length(); i++){
            wordEditorObject.getCurrentText().add(indexToAdd, new WordEditorCharacter(wordRemoved.charAt(i)));
            indexToAdd += 1;
        }
    }

    public void undo(){
        if(!wordEditorObject.getUndoStack().isEmpty()){
            WordEditorOperation operationToUndo = wordEditorObject.getUndoStack().pop();
            wordEditorObject.getRedoStack().push(operationToUndo);
            if(operationToUndo.getOperationName().equals("bold") || operationToUndo.getOperationName().equals("underline")
                    || operationToUndo.getOperationName().equals("italic")){
                undoStringFormatting(operationToUndo);
            }
            else{
                if(operationToUndo.getOperationName().equals("add")){
                    undoAddOperation(operationToUndo);
                }
                else{
                    undoRemoveOperation(operationToUndo);
                }
            }
            writeObjectToMetadataFile();
        }
    }

    public void redo(){
        if(!wordEditorObject.getRedoStack().isEmpty()){
            WordEditorOperation operationToRedo = wordEditorObject.getRedoStack().pop();
            if(operationToRedo.getOperationName().equals("add")){
                String wordToAdd = operationToRedo.getOperationParameters().get("word");
                if(operationToRedo.getOperationParameters().containsKey("position")){
                    int position = Integer.parseInt(operationToRedo.getOperationParameters().get("position"));
                    add(wordToAdd, position);
                }
                else{
                    add(wordToAdd);
                }
            }
            else{
                int fromPosition = Integer.parseInt(operationToRedo.getOperationParameters().get("fromPosition"));
                int toPosition = Integer.parseInt(operationToRedo.getOperationParameters().get("toPosition"));
                if(operationToRedo.getOperationName().equals("remove")){
                    remove(fromPosition, toPosition);
                }
                else if(operationToRedo.getOperationName().equals("bold")){
                    bold(fromPosition, toPosition);
                }
                else if(operationToRedo.getOperationName().equals("underline")){
                    underline(fromPosition, toPosition);
                }
                else{
                    italic(fromPosition, toPosition);
                }
            }
        }
    }

    public void print() {
        StringBuilder currentText = new StringBuilder();
        for(WordEditorCharacter character : wordEditorObject.getCurrentText()){
            String stringToAppend = character.getC() + "";
            if(character.isBold()){
                stringToAppend = "\033[1m" + stringToAppend + "\033[0m";
            }
            if(character.isItalics()){
                stringToAppend = "\033[3m" + stringToAppend + "\033[0m";
            }
            if(character.isUnderlined()){
                stringToAppend = "\033[4m" + stringToAppend + "\033[0m";
            }
            currentText.append(stringToAppend);
        }
        System.out.println(currentText.toString());
    }

}
