/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dto;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;


/**
 * @author vivek
 *
 */
public class Item {
    private int itemId;
    private final ReadOnlyStringWrapper itemName;
    
    public Item() {
        itemId = 0;
        itemName = new ReadOnlyStringWrapper(this, "itemName");
    }
    
    public Item(Item item) {
        this();
        itemId = item.getItemId();
        itemName.set(item.getItemName());
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int value) {
       if (value <= 0) {
           throw new IllegalArgumentException("Item Id value must be a positive integer.");
       }
        itemId = value;
    }
    
    public String getItemName() {
        return itemName.getValueSafe();
    }
    
    public void setItemName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name must be a non-null and non-blank value.");
        }
        
        itemName.set(name);
    }
    
    public ReadOnlyStringProperty itemNameProperty() {
        return itemName.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        String name = itemName.get();
       return (name == null ? "<Item Name Not Set>" : name);
    }

    @Override
    public int hashCode() {
        
      int id = getItemId();
      if (id != 0) {
          return id;
      }
      
        String name = itemName.get();
        if (name == null) {
            return super.hashCode();
        }
        
        return name.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Item)) {
            return false;
        }
        
        Item item = (Item) obj;
        if (this.getItemId() != 0 && item.getItemId() != 0) {
            return (this.getItemId() == item.getItemId());
        }
        
        String name = item.getItemName();
        String thisName = itemName.getValueSafe();
        
        if (name.isEmpty() || thisName.isEmpty()) {
            return false;
        }
        
        return thisName.equalsIgnoreCase(name);
    }
    
}
