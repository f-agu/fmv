package org.fagu.fmv.core;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;


/**
 * @author f.agu
 */
public class Hash {

	private final List<String> list;

	/**
	 * @param objects
	 */
	public Hash(Object... objects) {
		list = new ArrayList<>();
		for(Object obj : objects) {
			append(obj);
		}
	}

	/**
	 * @param object
	 */
	public Hash append(Object object) {
		if(object instanceof Hash) {
			append((Hash)object);
		} else {
			list.add(String.valueOf(object));
		}
		return this;
	}

	/**
	 * @param object
	 */
	public Hash append(int i) {
		list.add(Integer.toString(i));
		return this;
	}

	/**
	 * @param hash
	 */
	public Hash append(Hash hash) {
		list.addAll(hash.list);
		return this;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Hash ? ((Hash)obj).toString().equals(toString()) : false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for(String value : list) {
				md5.update(value.getBytes("UTF-8"));
			}
			return Base64.encodeBase64URLSafeString(md5.digest());
		} catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException(e); // NOSONAR
		}
	}
}
