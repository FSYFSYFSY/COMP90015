package org.code;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;


public class dictionary {
    private static Map<String, Set<String>> data = null;
    public void set(String path) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        data = objectMapper.readValue(new File(path), new TypeReference<Map<String, Set<String>>>() {});

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

        System.out.println("Create a dictionary");
    }

    public Map<String, Set<String>> get(){
        return data;
    }

    public String add(String key, String new_value){

        if(new_value.isBlank()){
            return "error";
        }

        else if (!data.containsKey(key)){
            data.put(key, new HashSet<>());
            data.get(key).add(new_value);
            return "success";
        }
        else {
            return "duplicate";
        }

    }

    public String add_exist(String key, String new_value) {
        if(new_value.isBlank()){
            return "error";
        }
        else if (!data.containsKey(key)) {
            return "notfound";
        }
        else{
            data.get(key).add(new_value);
            return "success";
        }
    }

    public String remove(String key) {
        if (!data.containsKey(key)) {
            return "notfound";
        }
        data.remove(key);
        return "success";

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
                return "success";
            }
            else{
                return "valueerror";
            }
        }
    }

    public Set<String> query(String key){
        return data.get(key);
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
