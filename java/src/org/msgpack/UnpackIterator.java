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
package org.msgpack;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnpackIterator implements Iterator<Object> {
	private Unpacker pac;
	private boolean have;
	private Object data;

	UnpackIterator(Unpacker pac) {
		this.pac = pac;
		this.have = false;
	}

	public boolean hasNext() {
		if(have) { return true; }
		try {
			while(true) {
				if(pac.execute()) {
					data = pac.getData();
					pac.reset();
					have = true;
					return true;
				}

				if(!pac.fill()) {
					return false;
				}
			}
		} catch (IOException e) {
			return false;
		}
	}

	public Object next() {
		if(!have && !hasNext()) {
			throw new NoSuchElementException();
		}
		have = false;
		return data;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}

