package com.cmcc;

import java.util.Arrays;
import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class AtlasEntitiesDemo implements AtlasDemoConstants {
	 public static final String LOCAL_CLUSTER = "cluster1";
	    public static final String REMOTE_CLUSTER = "cluster2";
	    public static final String LOCAL_WEBTABLE_NAME = "default.webtable@" + LOCAL_CLUSTER;
	    public static final String REMOTE_WEBTABLE_NAME = "default.webtable@" + REMOTE_CLUSTER;
	    public static final String LOCAL_CONTENTS_CF_NAME = "default.webtable.contents@" + LOCAL_CLUSTER;
	    public static final String REMOTE_CONTENTS_CF_NAME = "default.webtable.contents@" + REMOTE_CLUSTER;

	    private final AtlasClient atlasClient;

	    public AtlasEntitiesDemo(String atlasServiceUrl) {
	        atlasClient = new AtlasClient(new String[]{atlasServiceUrl}, new String[]{"admin", "admin"});
	    }

	    public static void main(String[] args){
	    	try {
		        AtlasEntitiesDemo atlasEntitiesDemo = new AtlasEntitiesDemo("http://10.136.106.76:21000");
//		        atlasEntitiesDemo.deleteEntity("7500d8c9-72c4-43bd-ae50-938972db35e8");
		        atlasEntitiesDemo.run();
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }

	    private void run() throws AtlasServiceException, JSONException {
	        // create an entity
//	        String localNamespaceId = createNamespace(LOCAL_CLUSTER);
	        
	        String localNamespaceId = "61903a60-c007-4845-89b0-aaefffd24485";

	        // create multiple entities - table, column families, columns
//	        String localTableId = createTable(localNamespaceId, LOCAL_CLUSTER, LOCAL_WEBTABLE_NAME, LOCAL_CONTENTS_CF_NAME);
	        String localTableId = "36c5292d-0644-4d1c-bb12-baaf771ca584";

	        // retrieve entities (by GUID and unique attributes)
	        retrieveEntity(localNamespaceId);
	        retrieveEntity(localTableId);
	        retrieveEntityByUniqueAttribute(HBASE_TABLE_TYPE, AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME,
	                LOCAL_WEBTABLE_NAME);

	        // update an entity - modify attribute value of some attribute
	        updateEntity(localTableId);
	        retrieveEntity(localTableId);

	        // add lineage related data
//	        String remoteNamespaceId = createNamespace(REMOTE_CLUSTER);
	        String remoteNamespaceId = "ff34e6c3-db69-46a5-9d74-23d9b1d54928";
	        String  remoteTableId   ="b407a886-0d7b-4d86-ae55-8250ce4918c3";
//	        String remoteTableId= createTable(remoteNamespaceId, REMOTE_CLUSTER, REMOTE_WEBTABLE_NAME, REMOTE_CONTENTS_CF_NAME);
	        String replicationProcessEntityId = createReplicationProcessEntity(localTableId, remoteTableId);
	        retrieveEntity(replicationProcessEntityId);

	        // delete an entity
	        deleteEntity(localTableId);
	        retrieveEntity(localTableId);
	    }

	    private String createReplicationProcessEntity(String localTableId, String remoteTableId)
	            throws AtlasServiceException {
	        System.out.println("Creating a replication instance for lineage.");
	        Referenceable referenceable = new Referenceable(HBASE_REPLICATION_PROCESS_TYPE);
	        String processName = "Replication: " + LOCAL_WEBTABLE_NAME + "->" + REMOTE_WEBTABLE_NAME;
	        referenceable.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, processName);
	        referenceable.set(AtlasClient.NAME, processName);
	        referenceable.set(AtlasClient.PROCESS_ATTRIBUTE_INPUTS,
	                Arrays.asList(AtlasUtils.getReferenceableId(localTableId, HBASE_TABLE_TYPE)));
	        referenceable.set(AtlasClient.PROCESS_ATTRIBUTE_OUTPUTS,
	                Arrays.asList(AtlasUtils.getReferenceableId(remoteTableId, HBASE_TABLE_TYPE)));
	        referenceable.set(REPLICATION_ENABLED, true);
	        referenceable.set(REPLICATION_SCHEDULE, "daily");

	        System.out.println(InstanceSerialization.toJson(referenceable, true));

	        List<String> entity = atlasClient.createEntity(Arrays.asList(referenceable));
	        AtlasUtils.printDelimiter();
	        return entity.get(0);
	    }

	    private void deleteEntity(String id) throws AtlasServiceException {
	        System.out.println("Deleting entity with GUID: " + id);
	        AtlasClient.EntityResult entityResult = atlasClient.deleteEntities(id);
	        for (String entity : entityResult.getDeletedEntities()) {
	            System.out.println("Entity deleted: " + entity);
	        }
	        AtlasUtils.printDelimiter();
	    }

	    private void updateEntity(String tableId) throws AtlasServiceException {
	        System.out.println("Updating table state to disabled");
	        Referenceable tableEntity = new Referenceable(HBASE_TABLE_TYPE);
	        tableEntity.set(TABLE_ATTRIBUTE_IS_ENABLED, false);
	        String entityJson = InstanceSerialization.toJson(tableEntity, true);
	        System.out.println(entityJson);
	        AtlasClient.EntityResult entityResult = atlasClient.updateEntity(tableId, tableEntity);
	        List<String> updateEntities = entityResult.getUpdateEntities();
	        for (String entity : updateEntities) {
	            System.out.println("Updated Entity ID: " + entity);
	        }
	        AtlasUtils.printDelimiter();
	    }

	    private void retrieveEntityByUniqueAttribute(
	            String typeName, String uniqueAttributeName, String uniqueAttributeValue) throws AtlasServiceException {
	        System.out.println("Retrieving entity with type: "
	                + typeName + "/" + uniqueAttributeName+"=" + uniqueAttributeValue);
	        Referenceable entity = atlasClient.getEntity(typeName, uniqueAttributeName, uniqueAttributeValue);
	        String entityJson = InstanceSerialization.toJson(entity, true);
	        System.out.println(entityJson);
	        AtlasUtils.printDelimiter();
	    }

	    private void retrieveEntity(String guid) throws AtlasServiceException {
	        System.out.println("Retrieving entity with GUID: " + guid);
	        Referenceable entity = atlasClient.getEntity(guid);
	        String entityJson = InstanceSerialization.toJson(entity, true);
	        System.out.println(entityJson);
	        AtlasUtils.printDelimiter();
	    }

	    private String createTable(String namespaceId, String clusterName, String tableName, String contentsCfName)
	            throws AtlasServiceException, JSONException {
	        System.out.println("Creating Table, Column Family & Column entities");
	        List<Referenceable> tableEntities = AtlasUtils.createTableEntities(clusterName, contentsCfName,
	                tableName, namespaceId);

	        JSONArray entitiesJson = new JSONArray(tableEntities.size());

	        System.out.print("[");
	        for (Referenceable entity : tableEntities) {
	            String entityJson = InstanceSerialization.toJson(entity, true);
	            System.out.print(entityJson);
	            System.out.println(",");
	            entitiesJson.put(entityJson);
	        }
	        System.out.println("]");

	        List<String> entitiesCreated = atlasClient.createEntity(tableEntities);
	        for (String entity : entitiesCreated) {
	            System.out.println("Entity created: " + entity);
	        }
	        AtlasUtils.printDelimiter();
	        return entitiesCreated.get(entitiesCreated.size()-1);
	    }


	    private String createNamespace(String clusterName) throws AtlasServiceException {
	        System.out.println("Creating namespace entity");
	        Referenceable hbaseNamespace = AtlasUtils.createNamespace(clusterName);
	        String hbaseNamespaceJson = InstanceSerialization.toJson(hbaseNamespace, true);
	        System.out.println(hbaseNamespaceJson);

	        List<String> entitiesCreated = atlasClient.createEntity(hbaseNamespace);
	        for (String entity : entitiesCreated) {
	            System.out.println("Entity created: " + entity);
	        }
	        
	        AtlasUtils.printDelimiter();
	        return entitiesCreated.get(0);
	    }
}
