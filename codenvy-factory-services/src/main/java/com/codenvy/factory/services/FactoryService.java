/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.factory.services;

import com.codenvy.factory.commons.AdvancedFactoryUrl;
import com.codenvy.factory.commons.FactoryUrlException;
import com.codenvy.factory.commons.Image;
import com.codenvy.factory.store.FactoryStore;

import org.apache.commons.fileupload.FileItem;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.Response.Status;

/** Service for factory rest api features */
@Path("/factory")
public class FactoryService {
    private static final Logger LOG = LoggerFactory.getLogger(FactoryService.class);
    private final FactoryStore        factoryStore;
    private final ServiceLinkInjector serviceLinkInjector;

    public FactoryService(FactoryStore factoryStore, ServiceLinkInjector serviceLinkInjector) {
        this.factoryStore = factoryStore;
        this.serviceLinkInjector = serviceLinkInjector;
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public AdvancedFactoryUrl saveFactory(Iterator<FileItem> factoryData, @Context UriInfo uriInfo) throws FactoryUrlException {
        try {
            Image image = null;
            AdvancedFactoryUrl factoryUrl = null;

            while (factoryData.hasNext()) {
                FileItem fileItem = factoryData.next();
                String fieldName = fileItem.getFieldName();
                if (fieldName.equals("factoryUrl")) {
                    JsonParser jsonParser = new JsonParser();
                    jsonParser.parse(fileItem.getInputStream());
                    JsonValue jsonValue = jsonParser.getJsonObject();
                    factoryUrl = ObjectBuilder.createObject(AdvancedFactoryUrl.class, jsonValue);
                }
                if (fieldName.equals("image")) {
                    image = new Image(fileItem.get(), fileItem.getContentType(), fileItem.getName());
                }
            }

            if (factoryUrl == null) {
                LOG.error("No factory URL information found in 'factoryUrl' section of multipart form-data");
                throw new FactoryUrlException(Status.BAD_REQUEST.getStatusCode(),
                                              "No factory URL information found in 'factoryUrl' section of multipart form-data");
            }

            // check that vcs value is correct (only git is supported for now)
            if (!"git".equals(factoryUrl.getVcs())) {
                throw new FactoryUrlException(Status.BAD_REQUEST.getStatusCode(),
                                              "Parameter vcs has illegal value. Only \"git\" is supported for now.");
            }

            // TODO rewrite this ugly backend stub
            Map<String, Object> factoryStoredData = factoryStore.saveFactory(factoryUrl, image);
            factoryUrl = (AdvancedFactoryUrl)factoryStoredData.get("factoryUrl");
            serviceLinkInjector.injectLinks(factoryUrl, (Set<String>)factoryStoredData.get("images"), uriInfo);

            return factoryUrl;
        } catch (IOException | JsonException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new FactoryUrlException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getLocalizedMessage(), e);
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public AdvancedFactoryUrl getFactory(@PathParam("id") String id, @Context UriInfo uriInfo) throws FactoryUrlException {
        // TODO rewrite this ugly backend stub
        Map<String, Object> factoryStoredData = factoryStore.getFactory(id);
        if (factoryStoredData == null) {
            LOG.error("Factory URL with id {} is not found.", id);
            throw new FactoryUrlException(Status.BAD_REQUEST.getStatusCode(),
                                          String.format("Factory URL with id %s is not found.", id));
        }

        // TODO rewrite this ugly backend stub
        AdvancedFactoryUrl factoryUrl = (AdvancedFactoryUrl)factoryStoredData.get("factoryUrl");
        serviceLinkInjector.injectLinks(factoryUrl, (Set<String>)factoryStoredData.get("images"), uriInfo);
        return factoryUrl;
    }

    @GET
    @Path("image/{id}")
    @Produces("image/*")
    public Response getImage(@PathParam("id") String id) throws FactoryUrlException {
        Image image = factoryStore.getImage(id);
        if (image == null) {
            LOG.error("Image with id {} is not found.", id);
            throw new FactoryUrlException(Status.BAD_REQUEST.getStatusCode(), String.format("Image with id %s is not found.", id));
        }

        return Response.ok(image.getImageData(), image.getMediaType()).build();
    }

    @GET
    @Path("{id}/snippet")
    @Produces({MediaType.TEXT_PLAIN})
    public String getFactorySnippet(@PathParam("id") String id, @QueryParam("type") String type,  @Context UriInfo uriInfo) throws FactoryUrlException {
        // TODO rewrite this ugly backend stub
        Map<String, Object> factoryStoredData = factoryStore.getFactory(id);
        if (factoryStoredData == null) {
            LOG.error("Factory URL with id {} is not found.", id);
            throw new FactoryUrlException(Status.BAD_REQUEST.getStatusCode(),
                                          String.format("Factory URL with id %s is not found.", id));
        }

        // TODO rewrite this ugly backend stub
        return "";
    }
}
