package net.jhorstmann.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JSONBuilder {
    public static class ObjectBuilder {

        private final JSONObject json;

        ObjectBuilder(JSONObject json) {
            this.json = json;
        }

        ObjectBuilder() {
            this(new JSONObject());
        }

        final ObjectBuilder put(String key, Object obj) {
            json.put(key, obj);
            return this;
        }

        public ObjectBuilder object(String key) {
            JSONObject obj;
            Object tmp = json.get(key);
            if (tmp instanceof JSONObject) {
                obj = (JSONObject) tmp;
            } else {
                obj = new JSONObject();
                json.put(key, obj);
            }
            return new ObjectBuilder(obj);
        }
        
        public ListBuilder list(String key) {
            ArrayList list;
            Object tmp = json.get(key);
            if (tmp instanceof ArrayList) {
                list = (ArrayList) tmp;
            } else {
                list = new ArrayList();
                json.put(key, list);
            }

            return new ListBuilder(list);
        }
        
        public ObjectBuilder propertyNull(String key) {
            return put(key, JSONNull.INSTANCE);
        }
        
        public ObjectBuilder propertyTrue(String key) {
            return put(key, Boolean.TRUE);
        }
        
        public ObjectBuilder propertyFalse(String key) {
            return put(key, Boolean.FALSE);
        }
        
        public ObjectBuilder property(String key, String value) {
            return put(key, value);
        }

        public ObjectBuilder property(String key, double value) {
            return put(key, Double.valueOf(value));
        }
        
        public ObjectBuilder property(String key, int value) {
            return put(key, Integer.valueOf(value));
        }

        public ObjectBuilder property(String key, boolean value) {
            return put(key, Boolean.valueOf(value));
        }
        
        public ObjectBuilder property(String key, ListBuilder builder) {
            return put(key, builder.getList());
        }
        
        public ObjectBuilder property(String key, ObjectBuilder builder) {
            return put(key, builder.getMap());
        }

        public Map<String, Object> getMap() {
            return Collections.unmodifiableMap(json);
        }

        @Override
        public String toString() {
            return json.toString();
        }
    }
    
    public static class ListBuilder {
        private final ArrayList list;
        
        ListBuilder(ArrayList list) {
            this.list = list;
        }
        
        ListBuilder(int capacity) {
            this(new ArrayList(capacity));
        }

        ListBuilder() {
            this(new ArrayList());
        }

        final ListBuilder add(Object obj) {
            list.add(obj);
            return this;
        }

        final ListBuilder set(int idx, Object obj) {
            int size = list.size();
            if (idx == size) {
                list.add(obj);
            } else {
                list.set(idx, obj);
            }
            return this;
        }
        
        public ListBuilder list() {
            ArrayList l = new ArrayList();
            add(l);
            return new ListBuilder(l);
        }

        public ListBuilder list(int idx) {
            int size = list.size();
            ArrayList lst;
            if (idx == size) {
                lst = new ArrayList();
                list.add(lst);
            } else {
                Object tmp = list.get(idx);
                if (tmp instanceof ArrayList) {
                    lst = (ArrayList) tmp;
                } else {
                    lst = new ArrayList();
                    list.set(idx, lst);
                }
            }
            return new ListBuilder(lst);
        }
        
        public ObjectBuilder object() {
            JSONObject obj = new JSONObject();
            list.add(obj);
            return new ObjectBuilder(obj);
        }

        public ObjectBuilder object(int idx) {
            int size = list.size();
            JSONObject obj;
            if (idx == size) {
                obj = new JSONObject();
                list.add(obj);
            } else {
                Object tmp = list.get(idx);
                if (tmp instanceof JSONObject) {
                    obj = (JSONObject) tmp;
                } else {
                    obj = new JSONObject();
                    list.set(idx, obj);
                }
            }
            return new ObjectBuilder(obj);
        }

        public ListBuilder itemNull() {
            return add(JSONNull.INSTANCE);
        }

        public ListBuilder itemNull(int idx) {
            return set(idx, JSONNull.INSTANCE);
        }

        public ListBuilder itemTrue() {
            return add(Boolean.TRUE);
        }

        public ListBuilder itemTrue(int idx) {
            return set(idx, Boolean.TRUE);
        }
        
        public ListBuilder itemFalse() {
            return add(Boolean.FALSE);
        }

        public ListBuilder itemFalse(int idx) {
            return set(idx, Boolean.FALSE);
        }
        
        public ListBuilder item(String value) {
            return add(value);
        }

        public ListBuilder item(int idx, String value) {
            return set(idx, value);
        }
        
        public ListBuilder item(double value) {
            return add(Double.valueOf(value));
        }

        public ListBuilder item(int idx, double value) {
            return set(idx, Double.valueOf(value));
        }
        
        public ListBuilder item(int value) {
            return add(Integer.valueOf(value));
        }

        public ListBuilder item(int idx, int value) {
            return set(idx, Integer.valueOf(value));
        }

        public ListBuilder item(ObjectBuilder builder) {
            return add(builder.getMap());
        }

        public ListBuilder item(int idx, ObjectBuilder builder) {
            return set(idx, builder.getMap());
        }

        public ListBuilder item(ListBuilder builder) {
            return add(builder.getList());
        }

        public ListBuilder item(int idx, ListBuilder builder) {
            return set(idx, builder.getList());
        }
        
        public List getList() {
            return Collections.unmodifiableList(list);
        }

        public int size() {
            return list.size();
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }

    public static ObjectBuilder object() {
        ObjectBuilder builder = new ObjectBuilder();
        return builder;
    }
    
    public static ListBuilder list() {
        ListBuilder builder = new ListBuilder();
        return builder;
    }
    
    public static ListBuilder list(int capacity) {
        ListBuilder builder = new ListBuilder(capacity);
        return builder;
    }
}