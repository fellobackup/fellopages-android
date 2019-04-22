/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 *
 */

package com.fellopages.mobileapp.classes.common.formgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;


abstract class FormWidget {

	protected String 		_property;
    protected  boolean      _hasValidator;
	protected String 		_displayText;
	protected LinearLayout 	_layout;
	protected FormActivity.FormWidgetToggleHandler _handler;
	
	protected HashMap<String, ArrayList<String>> _toggles;

	public FormWidget( Context context, String name, boolean hasValidator) {

		_layout = new LinearLayout( context );
		_layout.setLayoutParams( FormActivity.defaultLayoutParams );
		_layout.setOrientation( LinearLayout.VERTICAL );

		_property 		= name;
        this._hasValidator = hasValidator;
		_displayText 	= name.replace( "_", " ");
		_displayText 	= toTitleCase( _displayText );
	}




	// -----------------------------------------------
	//
	// view
	//
	// -----------------------------------------------
	/**
	 * return LinearLayout containing this widget's view elements
	 */
	public View getView() {
		return _layout;
	}
	
	/**
	 * toggles the visibility of this widget
	 * @param value
	 */
	public void setVisibility( int value ){
		_layout.setVisibility( value );
	}
	
	// -----------------------------------------------
	//
	// set / get value
	//
	// -----------------------------------------------
	
	/**
	 * returns value of this widget as String
	 */
	public String getValue() {
		return "";
	}

	/**
	 * sets value of this widget, method should be overridden in sub-class
	 * @param value
	 */
	public void setValue( String value ) {
		// -- override 
	}
	
	// -----------------------------------------------
	//
	// modifiers
	//
	// -----------------------------------------------
	
	/**
	 * sets the hint for the widget, method should be overriden in sub-class
	 */
	public void setHint( String value ){
		// -- override
	}
	
	// -----------------------------------------------
	//
	// property name mods
	//
	// -----------------------------------------------
	
	/**
	 * returns the un-modified name of the property this widget represents
	 */
	public String getPropertyName(){
		return _property;
	}
	
	/**
	 * returns a title case version of this property
	 * @return
	 */
	public String getDisplayText(){
		return _displayText;
	}
	
	/**
	 * takes a property name and modifies 
	 * @param s
	 * @return
	 */
	public String toTitleCase( String s ) {
		char[] chars = s.trim().toLowerCase().toCharArray();
		boolean found = false;
	 
		for (int i=0; i<chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i])) {
				found = false;
			}
		}
	 
		return String.valueOf(chars);
	}
	
	// -----------------------------------------------
	//
	// toggles
	//
	// -----------------------------------------------
	
	/**
	 * sets the list of toggles for this widgets
	 * the structure of the data looks like this:
	 * HashMap<value of property for visibility, ArrayList<list of properties to toggle on>>
	 */
	public void setToggles( HashMap<String, ArrayList<String>> toggles ) {
		_toggles = toggles;
	}
	
	/**
	 * return list of widgets to toggle on
	 * @return
	 */
	public ArrayList<String> getToggledOn() {
		if( _toggles == null ) return new ArrayList<>();
		
		if( _toggles.get( getValue() ) != null ) {
			return _toggles.get( getValue() );
		} else {
			return new ArrayList<>();
		}
	}
	
	/**
	 * return list of widgets to toggle off
	 * @return
	 */
	public ArrayList<String> getToggledOff() {
		ArrayList<String> result = new ArrayList<>();
		if( _toggles == null ) return result;
		
		Set<String> set = _toggles.keySet();
		
		 for (String key : set)
		 {
			 if( !key.equals( getValue() ) ) 
			 {
				ArrayList<String> list = _toggles.get(key);
				if( list == null ) return result;
				for( int i = 0; i < list.size(); i++ ) {
					result.add( list.get(i) );
				}
			}
		 }
		
		return result;
	}
	
	/**
	 * sets a handler for value changes
	 * @param handler
	 */
	public void setToggleHandler( FormActivity.FormWidgetToggleHandler handler ){
		_handler = handler;
	}

    public void setErrorMessage(String errorMessage){

    }

    public boolean is_hasValidator() {
        return _hasValidator;
    }

    public void set_hasValidator(boolean _hasValidator) {
        this._hasValidator = _hasValidator;
    }
}
