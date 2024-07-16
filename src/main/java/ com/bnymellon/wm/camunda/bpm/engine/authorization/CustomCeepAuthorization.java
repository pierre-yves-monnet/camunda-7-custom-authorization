package com.bnymellon.wm.camunda.bpm.engine.authorization;


import org.camunda.authorization.AuthorizationPlugIn;
import org.camunda.bpm.engine.authorization.AuthorizationQuery;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.impl.AbstractQuery;
import org.camunda.bpm.engine.impl.db.CompositePermissionCheck;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationManager;
import org.camunda.bpm.engine.management.ProcessDefinitionStatistics;
import org.camunda.bpm.engine.management.ProcessDefinitionStatisticsQuery;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.rest.ProcessDefinitionRestService;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import javax.ws.rs.core.Response;
import java.util.List;

@Configuration
public class CustomCeepAuthorization extends AuthorizationManager {
    Logger AppLog = LoggerFactory.getLogger(AuthorizationPlugIn.class.getName());

    private static final String CAMUNDA_ADMIN = "camunda-admin2";

    @Override
    public void checkAuthorization(CompositePermissionCheck compositePermissionCheck){
        String currentUserId = getCurrentUserId();
        AppLog.info("checkAuthorization with single cp input :{} userId{}",compositePermissionCheck, currentUserId);

        if(currentUserId != null) {
            compositePermissionCheck.getAllPermissionChecks().forEach(permissionCheck -> {
                AppLog.info("PermissionCheck permissionName: " + permissionCheck.getPermission().getName());
                AppLog.info("PermissionCheck permissionValue: " + permissionCheck.getPermission().getValue());
                AppLog.info("PermissionCheck resourceId: " + permissionCheck.getResourceId());
                AppLog.info("PermissionCheck resourceName: " + permissionCheck.getResource().resourceName());
                boolean isAuthorized = isAuthorized(permissionCheck.getPermission(), permissionCheck.getResource(), permissionCheck.getResourceId());
                if(!isAuthorized) {
                    throw new RestException(Response.Status.FORBIDDEN, "User is not authorized to perform the action - " + permissionCheck.getPermission().getName()
                            + " - on the resource - " + permissionCheck.getResource().resourceName() + " - with resourceId - " + permissionCheck.getResourceId());
                }
            });
        }
    }

    @Override
    public void checkAuthorization(Permission permission, Resource resource, String resourceId){
        String currentUserId = getCurrentUserId();
        AppLog.info("checkAuthorization with 3 inputs: Permission:[{}] Resource:[{}] resourceId:[{}] userId:[{}]",
                permission.getName(), resource.resourceName(), resourceId, currentUserId);
        boolean isAuthorized = this.isAuthorized(permission, resource, resourceId);
        if(currentUserId != null && !isAuthorized){
            throw new RestException(Response.Status.FORBIDDEN, "User is not authorized to perform the action - "+permission.getName()
                    + " - on the resource - "+resource.resourceName() + " - with resourceId - "+resourceId);
        }
    }

    @Override
    public boolean isAuthorized(Permission permission, Resource resource, String resourceId){
        String currentUserId = getCurrentUserId();
        AppLog.info("isAuthorized: Permission[{}] Resource:[{}] resourceId:[{}] userId:[{}]", permission.getName(), resource.resourceName(),
                resourceId, currentUserId);
        if(currentUserId != null) {
            List<String> groupIds = getCurrentUserGroupIds();
            if(groupIds != null && !groupIds.contains(CAMUNDA_ADMIN)) {
                if(permission.getName().equalsIgnoreCase("READ_HISTORY")){ //This is to restrict access to delete process definition id
                    return false;
                }
                /**
                if (!permission.getName().toUpperCase().startsWith("READ")) {
                    return false;
                }
                 */
                if (permission.getName().toUpperCase().startsWith("SUSPEND_INSTANCE")) {
                    return false;
                }
                if (permission.getName().toUpperCase().startsWith("DELETE_INSTANCE")) {
                    return false;
                }
                if (permission.getName().toUpperCase().startsWith("UPDATE_VARIABLE")) {
                    return false;
                }
                //Use the resourceName Filter to restrict from loading the taskList
                if (resource.resourceName().equalsIgnoreCase("Filter")) {
                    return false;
                }
            }
        }
        //Decide in this method if user(belonging to the group) is authorized to access the resource(any Type)
        //Below camunda-admin is just for test purpose. This logic should be written in reverse way to check workflows not part of the user's groups
        /*if(groupIds != null && groupIds.contains("camunda-admin")){
            if(resource.resourceName().equalsIgnoreCase("ProcessDefinition")
                    && resourceId != null && resourceId.equalsIgnoreCase("reporting-request")) {
                return false;
            }
            return true;
        }*/
        return true;
    }

    @Override
    public void configureQuery(AbstractQuery query, Resource resource, String queryParam, Permission permission) {
        String currentUserId = getCurrentUserId();
        AppLog.info("Current User Id in configQuery mtd with 4 inputs - "+currentUserId);

        if(currentUserId != null){
            boolean isAuthorized = this.isAuthorized(permission, resource, null);
            if(!isAuthorized) {
                throw new RestException(Response.Status.FORBIDDEN, "User is not authorized to perform the action - " + permission.getName()
                        + " - on the resource - " + resource.resourceName());
            }
        }
    }

    @Override
    public void configureQuery(ListQueryParameterObject listQueryParameterObject){
        String currentUserId = getCurrentUserId();
        AppLog.info("Current User Id in configQuery mtd ListQueryParameterObject - "+currentUserId);


        if(currentUserId != null)
        {
            //this gets called during deleting all version of proc def from cockpit dashboard main page.
            AppLog.info("User is not null. Check if ProcessInstanceQuery");
            if(listQueryParameterObject instanceof ProcessInstanceQuery) {
                AppLog.info("Inside if of ProcessInstanceQuery");
                //throw new RestException(Response.Status.FORBIDDEN, "User is not authorized");
            }
            else if(listQueryParameterObject instanceof ProcessDefinitionQuery){
                AppLog.info("Inside if of ProcessDefinitionQuery");
            }
            else if(listQueryParameterObject instanceof DeploymentQuery){
                AppLog.info("Inside if of DeploymentQuery");
            }
            else if(listQueryParameterObject instanceof ProcessDefinitionStatisticsQuery){
                AppLog.info("Inside if of ProcessDefinitionStatisticsQuery");
            }
            else if(listQueryParameterObject instanceof HistoricProcessInstanceQuery){
                AppLog.info("Inside if of HistoricProcessInstanceQuery");
            }
            else if(listQueryParameterObject instanceof ProcessDefinitionRestService){
                AppLog.info("Inside if of HistoricProcessInstanceQuery");
            }
            else if(listQueryParameterObject instanceof ProcessDefinitionStatistics){
                AppLog.info("Inside if of HistoricProcessInstanceQuery");
            }
            else{
                AppLog.info("Inside else of ListQueryParameterObject");
            }
        }

        //Below if block does not allow non-admins to access "Manage Authorization" page in Admin webapp
        if(currentUserId != null && listQueryParameterObject instanceof AuthorizationQuery){
            AppLog.info("Inside AuthorizationQuery method");
            List<String> groupIds = getCurrentUserGroupIds();
            if(groupIds != null && !groupIds.contains("camunda-admin")){
                throw new RestException(Response.Status.FORBIDDEN, "User is not authorized");
            }
        }
    }

    private String getCurrentUserId(){
        final Authentication currentAuthentication =getCurrentAuthentication();
        if(currentAuthentication != null && currentAuthentication.getUserId() != null){
            return currentAuthentication.getUserId();
        }
        return null;
    }

    private List<String> getCurrentUserGroupIds(){
        final Authentication currentAuthentication =getCurrentAuthentication();
        if(currentAuthentication != null && currentAuthentication.getUserId() != null && currentAuthentication.getGroupIds() != null){
            return currentAuthentication.getGroupIds();
        }
        return null;
    }
}

