//
// MessagePack for Java
//
// Copyright (C) 2009-2010 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.schema;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.msgpack.*;

public class StringSchema extends Schema {
	public StringSchema() {
		super("string");
	}

	@Override
	public String getFullName() {
		return "String";
	}

	@Override
	public void pack(Packer pk, Object obj) throws IOException {
		// FIXME instanceof GenericObject
		if(obj instanceof String) {
			try {
				byte[] d = ((String)obj).getBytes("UTF-8");
				pk.packRaw(d.length);
				pk.packRawBody(d);
			} catch (UnsupportedEncodingException e) {
				throw MessageTypeException.invalidConvert(obj, this);
			}

		} else if(obj instanceof byte[]) {
			byte[] d = (byte[])obj;
			pk.packRaw(d.length);
			pk.packRawBody(d);

		} else if(obj instanceof ByteBuffer) {
			ByteBuffer d = (ByteBuffer)obj;
			if(!d.hasArray()) {
				throw MessageTypeException.invalidConvert(obj, this);
			}
			pk.packRaw(d.capacity());
			pk.packRawBody(d.array(), d.position(), d.capacity());

		} else if(obj == null) {
			pk.packNil();

		} else {
			throw MessageTypeException.invalidConvert(obj, this);
		}
	}

	@Override
	public Object convert(Object obj) throws MessageTypeException {
		// FIXME instanceof GenericObject
		if(obj instanceof String) {
			return obj;

		} else if(obj instanceof byte[]) {
			try {
				return new String((byte[])obj, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw MessageTypeException.invalidConvert(obj, this);
			}

		} else if(obj instanceof ByteBuffer) {
			ByteBuffer d = (ByteBuffer)obj;
			try {
				if(d.hasArray()) {
					return new String(d.array(), d.position(), d.capacity(), "UTF-8");
				} else {
					byte[] v = new byte[d.capacity()];
					int pos = d.position();
					d.get(v);
					d.position(pos);
					return new String(v, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				throw MessageTypeException.invalidConvert(obj, this);
			}

		} else {
			throw MessageTypeException.invalidConvert(obj, this);
		}
	}

	@Override
	public Object createFromRaw(byte[] b, int offset, int length) {
		try {
			return new String(b, offset, length, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

