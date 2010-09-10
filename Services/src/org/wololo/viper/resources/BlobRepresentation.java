package org.wololo.viper.resources;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import com.google.appengine.api.datastore.Blob;

class BlobRepresentation extends OutputRepresentation {

	Blob blob;

	BlobRepresentation(Blob blob) {
		super(MediaType.IMAGE_PNG);
		this.blob = blob;
	}

	@Override
	public void write(OutputStream arg0) throws IOException {
		byte[] bytes = blob.getBytes();

		arg0.write(bytes);
	}
}