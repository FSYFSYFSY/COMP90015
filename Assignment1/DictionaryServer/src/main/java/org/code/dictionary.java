package org.code;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;


public class dictionary {
    private static Map<String, Set<String>> data = null;
    public void set(String path) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        if (path == null || path.isEmpty() || !new File(path).exists()) {
            InputStream inputStream = getClass().getResourceAsStream("/dictionary.json");
            if (inputStream == null) {
                throw new FileNotFoundException("dictionary.json not found inside JAR!");
            }
            data = objectMapper.readValue(inputStream, new TypeReference<Map<String, Set<String>>>() {});
            System.out.println("Loaded dictionary from JAR resource.");
        }
        else{
            data = objectMapper.readValue(new File(path), new TypeReference<Map<String, Set<String>>>() {});
            System.out.println("Loaded dictionary from file: " + path);
        }

        Map<String, Set<String>> lowerCaseData = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : data.entrySet()) {
            String lowerKey = entry.getKey().toLowerCase();
            Set<String> lowerValues = new HashSet<>();
            for (String value : entry.getValue()) {
                lowerValues.add(value.toLowerCase());
            }
            lowerCaseData.put(lowerKey, lowerValues);
        }
        data = lowerCaseData;
    }

    public Map<String, Set<String>> get(){
        return data;
    }

    public String add(String key, String new_value){

        if (!data.containsKey(key)){
            data.put(key, new HashSet<>());
            data.get(key).add(new_value);
            return "Successfully added " + key + " : "+ new_value;
        }
        else {
            return "word already existed, please use add_exist";
        }
    }

    public String add_exist(String key, String new_value) {
        if (!data.containsKey(key)) {
            return "notfound";
        }
        else{
            data.get(key).add(new_value);
            return "Successfully add_exist " + key + " : "+ new_value;
        }
    }

    public String remove(String key) {
        if (!data.containsKey(key)) {
            return "notfound";
        }
        data.remove(key);
        return "Successfully removed " + key;

    }

    public String update(String key, String value, String new_value) {
        if(new_value.isBlank()){
            return "error";
        }
        else if (!data.containsKey(key)) {
            return "notfound";
        }
        else{
            if(data.get(key).remove(value)){
                data.get(key).add(new_value);
                return "Successfully update " + key + " : "+ new_value + " from " + value;
            }
            else{
                return "value error, no target value";
            }
        }
    }

    public String query(String key){

       int counter = 1;

        if (!data.containsKey(key)){
            return "notfound";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : data.get(key)) {

            s = Integer.toString(counter) + ". " + s + "   |   ";
            sb.append(s);

            counter++;
        }
        String result = sb.toString();
        return result;
    }

    public void print(){
        for (Map.Entry<String, Set<String>> entry : data.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /*
    public static void main(String[] args) throws Exception {
        dictionary dict = new dictionary();
        dict.set("src/dictionary.json");
        dict.add("Fsy", "Huntley");
        dict.print();
    }
     */
}
