/**
 * Copyright (c) 2013, Sana
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sana nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL Sana BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sana.android.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.sana.Model;

import android.content.ContentValues;
import android.net.Uri;

/**
 * @author Sana Development
 *
 */
public abstract class TableHelper<T extends Model> implements  CreateHelper, InsertHelper, 
	SortHelper,UpdateHelper, UpgradeHelper
{
	public static final String TAG = TableHelper.class.getSimpleName();

	private final String table;
	private final String fColumn;
	private final String defaultExtension;
	private final Class<T> model;
	
	protected TableHelper(Class<T> klazz){
		this( klazz, null, null);
	}
	
	protected TableHelper( Class<T> klazz, String fileColumn, String extension){
		this.model = klazz;
		this.table = pluralize(klazz.getSimpleName().toLowerCase(Locale.US));
		this.fColumn = fileColumn;
		this.defaultExtension = extension;
	}
	
	private String pluralize(String in){
		String out = in.toLowerCase(Locale.US);
		return out + "s";
	}
	
	/**
	 * Returns the name of the table for based on the {@link android.netUri Uri}.
	 * 
	 * @param uri The uri to match against.
	 * @return A table name.
	 */
	public String getTable(){
		return table;
	}
	
	/**
	 * Returns the name of the column in this table where a file path is stored.
	 * @return The name of the file column
	 * @throws UnsupportedOperationExeception if no file column is defined.
	 */
	public String getFileColumn(){
		if(fColumn == null)
			throw new UnsupportedOperationException("No file column defined");
		else
			return fColumn;
	}

	/**
	 * Returns a default extension if this table has a column for storing files.
	 * @return The default extension.
	 * @throws UnsupportedOperationExeception if no file column is defined.
	 */
	public String getFileExtension(){
		if(fColumn == null)
			throw new UnsupportedOperationException("No file type defined");
		else
			return defaultExtension;
	}

	/**
	 * Returns the Model class this table represents.
	 * 
	 * @return
	 */
	public Class<T> getModelClass(){
		return model;
	}
	
	/* (non-Javadoc)
	 * @see org.sana.android.db.SortHelper#onSort(android.net.Uri)
	 */
	@Override
	public String onSort(Uri uri) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sana.android.db.InsertHelper#onInsert(android.net.Uri, android.content.ContentValues)
	 */
	/**
	 * Sets the creation and modification time to the current date time 
	 * formatted as {@link org.sana.Model#DATE_FORMAT}
	 */
	@Override
	public ContentValues onInsert(ContentValues values) {
		ContentValues validValues = new ContentValues();
		String value = new SimpleDateFormat(Model.DATE_FORMAT, 
				Locale.US).format(new Date());
		validValues.put(Model.UUID, UUID.randomUUID().toString());
		validValues.put(Model.CREATED, value);
		validValues.put(Model.MODIFIED, value);
		validValues.putAll(values);
		return validValues;
	}

	/* (non-Javadoc)
	 * @see org.sana.android.db.UpdateHelper#onUpdate(java.lang.String, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	/**
	 * Sets the modification time to the current date time if not present,
	 * formatted as {@link org.sana.Model#DATE_FORMAT}
	 */
	@Override
	public ContentValues onUpdate(Uri uri, ContentValues values) {
		ContentValues validValues = new ContentValues();
		if(!values.containsKey(Model.MODIFIED)){
			String value = new SimpleDateFormat(Model.DATE_FORMAT, 
					Locale.US).format(new Date());
			validValues.put(Model.MODIFIED, value);
		}
		validValues.putAll(values);
		return validValues;
	}
}
