/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * $Id$
 */
package org.jboss.seam.mock.faces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.NoSuchElementException;
import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * @author Gavin King
 * @author Thomas Heute
 * @author Dan Allen
 */
public class MockFacesContext extends FacesContext
{
   private Application application;

   private UIViewRoot viewRoot;

   /**
    * Store mapping of clientId to ArrayList of FacesMessage instances.  The
    * null key is used to represent FacesMessage instances that are not
    * associated with a clientId instance.
    */
   private final Map<String, List<FacesMessage>> messages = new LinkedHashMap<String, List<FacesMessage>>();

   private Map<Object, Object> attributes = new HashMap<Object, Object>();

   private ExternalContext externalContext;

   private ResponseWriter responseWriter;

   private RenderKitFactory renderKitFactory;

   private ELContext elContext;

   private boolean renderResponse;

   private boolean responseComplete;

   private PhaseId currentPhaseId;

   private boolean postback;

   public MockFacesContext()
   {
      attributes.put(UINamingContainer.SEPARATOR_CHAR_PARAM_NAME, ':');
   }

   public MockFacesContext(boolean postback)
   {
      this();
      this.postback = postback;
   }

   public MockFacesContext(Application application)
   {
      this();
      this.application = application;
   }

   public MockFacesContext(Application application, boolean postback)
   {
      this();
      this.application = application;
      this.postback = postback;
   }

   public MockFacesContext(ExternalContext externalContext, Application application)
   {
      this();
      this.externalContext = externalContext;
      this.application = application;
   }

   // Create a MockFacesContext using a ApplicationFactory to get the
   // Application
   public MockFacesContext(ExternalContext externalContext)
   {
      this();
      this.application = ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication();
      this.renderKitFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
      this.externalContext = externalContext;
   }

   @Override
   public Application getApplication()
   {
      return application;
   }

   @Override
   public Iterator getClientIdsWithMessages()
   {
      return messages.keySet().iterator();
   }

   @Override
   public ExternalContext getExternalContext()
   {
      return externalContext;
   }

   @Override
   public Severity getMaximumSeverity()
   {
      Severity max = null;
      for (List<FacesMessage> messagesForKey : messages.values())
      {
         for (FacesMessage msg : messagesForKey)
         {
            if (msg.getSeverity() == FacesMessage.SEVERITY_FATAL)
            {
               return FacesMessage.SEVERITY_FATAL;
            }
            else if (max == null || msg.getSeverity().compareTo(max) > 0)
            {
               max = msg.getSeverity();
            }
         }
      }
      return max;
   }

   @Override
   public Iterator getMessages()
   {
      return messages.size() > 0 ? new FacesMessagesIterator(messages) : Collections.<FacesMessage>emptyList().iterator();
   }

   @Override
   public Iterator getMessages(String clientId)
   {
      List<FacesMessage> messagesForKey = messages.get(clientId);
      return messagesForKey != null ? messagesForKey.iterator() : Collections.<FacesMessage>emptyList().iterator();
   }

   @Override
   public List<FacesMessage> getMessageList()
   {
      List<FacesMessage> aggregatedMessages = new ArrayList<FacesMessage>();
      for (List<FacesMessage> messagesForKey : messages.values())
      {
         aggregatedMessages.addAll(messagesForKey);
      }
      return Collections.unmodifiableList(aggregatedMessages);
   }

   @Override
   public List<FacesMessage> getMessageList(String clientId)
   {
      return messages.containsKey(clientId) ? Collections.unmodifiableList(messages.get(clientId)) : Collections.<FacesMessage>emptyList();
   }

   @Override
   public Map<Object, Object> getAttributes()
   {
      return attributes;
   }

   @Override
   public RenderKit getRenderKit()
   {
      if (getViewRoot() == null || getViewRoot().getRenderKitId() == null)
      {
         return MockRenderKit.INSTANCE;
      }
      else
      {
         return renderKitFactory.getRenderKit(this, getViewRoot().getRenderKitId());
      }
   }

   @Override
   public boolean getRenderResponse()
   {
      return renderResponse;
   }

   @Override
   public boolean getResponseComplete()
   {
      return responseComplete;
   }

   @Override
   public ResponseStream getResponseStream()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setResponseStream(ResponseStream stream)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResponseWriter getResponseWriter()
   {
      return responseWriter;
   }

   @Override
   public void setResponseWriter(ResponseWriter writer)
   {
      responseWriter = writer;
   }

   @Override
   public UIViewRoot getViewRoot()
   {
      return viewRoot;
   }

   @Override
   public void setViewRoot(UIViewRoot vr)
   {
      viewRoot = vr;
   }

   @Override
   public void addMessage(String clientId, FacesMessage msg)
   {
      if (!messages.containsKey(clientId))
      {
         List<FacesMessage> messagesForKey = new ArrayList<FacesMessage>(1);
         messagesForKey.add(msg);
         messages.put(clientId, messagesForKey);
      }
      else
      {
         messages.get(clientId).add(msg);
      }
   }

   @Override
   public void release()
   {
      setCurrentInstance(null);
      MockFacesContextFactory.setFacesContext(null);
   }

   @Override
   public void renderResponse()
   {
      renderResponse = true;
   }

   @Override
   public void responseComplete()
   {
      responseComplete = true;
   }

   @Override
   public PhaseId getCurrentPhaseId()
   {
      return currentPhaseId;
   }

   @Override
   public void setCurrentPhaseId(PhaseId phaseId)
   {
      this.currentPhaseId = phaseId;
   }

   @Override
   public boolean isPostback()
   {
      return postback;
   }

   public void setPostback(boolean postback)
   {
      this.postback = postback;
   }

   public MockFacesContext setCurrent()
   {
      setCurrentInstance(this);

      MockFacesContextFactory.setFacesContext(this);
      return this;
   }

   public MockFacesContext createViewRoot()
   {
      viewRoot = new UIViewRoot();
      viewRoot.setRenderKitId(getApplication().getViewHandler().calculateRenderKitId(this));
      return this;
   }

   @Override
   public ELContext getELContext()
   {
      /*
      if (elContext == null)
      {
      elContext = EL.createELContext(EL.createELContext(), getApplication().getELResolver());
      elContext.putContext(FacesContext.class, this);
      }
      return elContext;
       */
      return elContext;
   }

   public void setELContext(ELContext elContext)
   {
      this.elContext = elContext;
   }

   private static final class FacesMessagesIterator implements Iterator<FacesMessage>
   {
      private Map<String, List<FacesMessage>> messages;

      private int keyIndex = -1;

      private int numKeys;

      private Iterator<FacesMessage> messagesForKey;

      private Iterator<String> keys;

      FacesMessagesIterator(Map<String, List<FacesMessage>> messages)
      {
         this.messages = messages;
         numKeys = messages.size();
         keys = messages.keySet().iterator();
      }

      public boolean hasNext()
      {
         if (keyIndex == -1)
         {
            keyIndex++;
            messagesForKey = messages.get(keys.next()).iterator();
         }
         while (!messagesForKey.hasNext())
         {
            keyIndex++;
            if ((keyIndex) < numKeys)
            {
               messagesForKey = messages.get(keys.next()).iterator();
            }
            else
            {
               return false;
            }
         }
         return messagesForKey.hasNext();
      }

      public FacesMessage next()
      {
         if (keyIndex >= numKeys)
         {
            throw new NoSuchElementException();
         }
         if (messagesForKey != null && messagesForKey.hasNext())
         {
            return messagesForKey.next();
         }
         else
         {
            if (!this.hasNext())
            {
               throw new NoSuchElementException();
            }
            else
            {
               return messagesForKey.next();
            }
         }
      }

      public void remove()
      {
         if (keyIndex == -1)
         {
            throw new IllegalStateException();
         }
         messagesForKey.remove();
      }
   }
}
