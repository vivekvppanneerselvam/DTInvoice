/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dto;


/**
 * @author vivek
 *
 */
public class EntityEditedEvent extends java.util.EventObject {
   private final Object entityEdited;
   
    public EntityEditedEvent(Object source, Object entityEdited) {
        super(source);
        this.entityEdited = entityEdited;
    }
    
    public Object getEntityEdited() {
        return entityEdited;
    }
    
}
