package org.jboss.test.faces.mockito;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.faces.render.RenderKit;

public class MockFacesContext extends FacesContext {

    public void setCurrentContext(FacesContext context) {
        setCurrentInstance(context);
    }

    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public Iterator<String> getClientIdsWithMessages() {
        return null;
    }

    @Override
    public ExternalContext getExternalContext() {
        return null;
    }

    @Override
    public Severity getMaximumSeverity() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        return null;
    }

    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    @Override
    public boolean getRenderResponse() {
        return false;
    }

    @Override
    public boolean getResponseComplete() {
        return false;
    }

    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    @Override
    public void setResponseStream(ResponseStream responseStream) {

    }

    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
    }

    @Override
    public UIViewRoot getViewRoot() {
        return null;
    }

    @Override
    public void setViewRoot(UIViewRoot root) {
    }

    @Override
    public void addMessage(String clientId, FacesMessage message) {
    }

    @Override
    public void release() {
        setCurrentInstance(null);
    }

    @Override
    public void renderResponse() {
    }

    @Override
    public void responseComplete() {
    }

    @Override
    public Map<Object, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public PartialViewContext getPartialViewContext() {
        return null;
    }

    @Override
    public ELContext getELContext() {
        return null;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return null;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    }

    @Override
    public List<FacesMessage> getMessageList() {
        return Collections.emptyList();
    }

    @Override
    public List<FacesMessage> getMessageList(String clientId) {
        return Collections.emptyList();
    }

    @Override
    public boolean isValidationFailed() {
        return false;
    }

    @Override
    public boolean isPostback() {
        return false;
    }

    @Override
    public void validationFailed() {
    }

    @Override
    public PhaseId getCurrentPhaseId() {
        return null;
    }

    @Override
    public void setCurrentPhaseId(PhaseId currentPhaseId) {
    }

    @Override
    public void setProcessingEvents(boolean processingEvents) {
    }

    @Override
    public boolean isProcessingEvents() {
        return false;
    }

    @Override
    public boolean isProjectStage(ProjectStage stage) {
        return false;
    }
    
    
}
