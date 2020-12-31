package com.cmcc;

import java.util.ArrayList;
import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.persistence.Id;

import com.google.common.collect.ImmutableList;

public class AtlasEntitiesTest {
	
	 final AtlasClient atlasClient = new AtlasClient
	            (new String[]{"http://10.136.106.76:21000"},
	                    new String[]{"admin",
	                            "admin"});
	 static final String DATABASE_TYPE = "DB_Sync";
	 static final String COLUMN_TYPE = "Column_Sync";
	 static final String TABLE_TYPE = "Table_Sync";
	 static final String VIEW_TYPE = "View_Sync";
	 static final String PROCESSTYPE = "Process";
	 public static final String DB_ATTRIBUTE = "db";
	 static final String STORAGE_DESC_TYPE = "StorageDesc";
	 public static final String COLUMNS_ATTRIBUTE = "columns";
	 public static final String INPUT_TABLES_ATTRIBUTE = "inputTables";

	public static void main(String[] args) {
		AtlasEntitiesTest entiyTest = new AtlasEntitiesTest();
		try {
			entiyTest.createEntities();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
     * 创建实例并返创建的Id对象
     * @param referenceable
     * @return
     * @throws Exception
     */
    private Id createInstance(Referenceable referenceable) throws Exception {
        String typeName = referenceable.getTypeName();
        String entityJSON = InstanceSerialization.toJson(referenceable, true);
        System.out.println("Submitting new entity= " + entityJSON);
        List<String> guids = atlasClient.createEntity(entityJSON);
        System.out.println("created instance for type " + typeName + ", guid: " + guids);
        return new Id(guids.get(guids.size() - 1), referenceable.getId().getVersion(),
                referenceable.getTypeName());
    }

    /**
     * 创建数据库实例并返创建的数据库Id对象
     * @param name
     * @param description
     * @param owner
     * @param locationUri
     * @param traitNames
     * @return
     * @throws Exception
     */
    Id database(String name, String description, String owner, String locationUri, String... traitNames)
            throws Exception {
        Referenceable referenceable = new Referenceable(DATABASE_TYPE, traitNames);
        referenceable.set("name", name);
        referenceable.set("description", description);
        referenceable.set("owner", owner);
        referenceable.set("locationUri", locationUri);
        referenceable.set("createTime", System.currentTimeMillis());

        return createInstance(referenceable);
    }

    /**
     * 创建列的实例并返创建的列的实例对象
     * @param name
     * @param dataType
     * @param comment
     * @param traitNames
     * @return
     * @throws Exception
     */
    Referenceable column(String name, String dataType, String comment, String... traitNames) throws Exception {
        Referenceable referenceable = new Referenceable(COLUMN_TYPE, traitNames);
        referenceable.set("name", name);
        referenceable.set("dataType", dataType);
        referenceable.set("comment", comment);

        return referenceable;
    }

    /**
     * 创建表的实例并返创建的表的Id对象
     * @param name
     * @param description
     * @param dbId
     * @param sd
     * @param owner
     * @param tableType
     * @param columns
     * @param traitNames
     * @return
     * @throws Exception
     */
    Id table(String name, String description, Id dbId, Referenceable sd, String owner, String tableType,
             List<Referenceable> columns, String... traitNames) throws Exception {
        Referenceable referenceable = new Referenceable(TABLE_TYPE, traitNames);
        referenceable.set("name", name);
        referenceable.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, name);
        referenceable.set("description", description);
        referenceable.set("owner", owner);
        referenceable.set("tableType", tableType);
        referenceable.set("createTime", System.currentTimeMillis());
        referenceable.set("lastAccessTime", System.currentTimeMillis());
        referenceable.set("retention", System.currentTimeMillis());
        referenceable.set("db", dbId);
        referenceable.set("sd", sd);
        referenceable.set("columns", columns);

        return createInstance(referenceable);
    }

    /**
     * 创建视图的实例并返创建的视图的Id对象
     * @param name
     * @param dbId
     * @param inputTables
     * @param traitNames
     * @return
     * @throws Exception
     */
    Id view(String name, Id dbId, List<Id> inputTables, String... traitNames) throws Exception {
        Referenceable referenceable = new Referenceable(VIEW_TYPE, traitNames);
        referenceable.set("name", name);
        referenceable.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, name);
        referenceable.set("db", dbId);

        referenceable.set(INPUT_TABLES_ATTRIBUTE, inputTables);

        return createInstance(referenceable);
    }
    
    /**
     * 创建进程的对象
     * @param name
     * @param description
     * @param owner
     * @param locationUri
     * @param inputs
     * @param outputs
     * @param traitNames
     * @return
     * @throws Exception
     */
    Id process(String name, String description, String owner,List<Id> inputs,List<Id> outputs, String... traitNames)throws Exception{
    	 Referenceable referenceable = new Referenceable(PROCESSTYPE, traitNames);
    	 referenceable.set("owner", owner);
    	 referenceable.set("createTime", "2020-10-05T10:32:21.0Z");
    	 referenceable.set("updateTime", "");
    	 referenceable.set("qualifiedName", "people@process@mysql://192.168.1.1:3306");
    	 referenceable.set("name", name);
    	 referenceable.set("description", description);
    	 referenceable.set("comment", "test atlas Process");
    	 referenceable.set("contact_info", " atlas Process");
    	 referenceable.set("inputs", inputs);
    	 referenceable.set("outputs", outputs);
    	 
    	 return createInstance(referenceable);

    }

    /**
     * 原始存储描述符
     * @param location
     * @param inputFormat
     * @param outputFormat
     * @param compressed
     * @return
     * @throws Exception
     */
    Referenceable storageDescriptor(String location, String inputFormat, String outputFormat, boolean compressed)
            throws Exception {
        Referenceable referenceable = new Referenceable(STORAGE_DESC_TYPE);
        referenceable.set("location", location);
        referenceable.set("inputFormat", inputFormat);
        referenceable.set("outputFormat", outputFormat);
        referenceable.set("compressed", compressed);

        return referenceable;
    }


    public void createEntities() throws Exception {
        //创建数据库实例
        Id syncDB = database("sy_sync_test", "Sync Database", "root", "");
        //存储描述符
        Referenceable sd =
                storageDescriptor("", "TextInputFormat", "TextOutputFormat",
                        true);
        //创建列实例
        //1、数据源
        List<Referenceable> databaseColumns = ImmutableList
                .of(column("id", "long", "id"),
                        column("name", "string", "name"),
                        column("type", "string", "type"),
                        column("url", "string", "url"),
                        column("database_name", "string", "database name"),
                        column("username", "string", "username"),
                        column("password","string","password"),
                        column("description", "string", "description"),
                        column("create_time", "string", "create time"),
                        column("update_time", "string", "update time"),
                        column("create_id", "long", "user id"),
                        column("update_id", "long", "user id"));
        //2、同步文件夹
        List<Referenceable> syncFolderColumns = ImmutableList
                .of(column("id", "long", "id"),
                        column("name", "string", "name"),
                        column("description", "string", "description"),
                        column("create_time", "string", "create time"),
                        column("update_time", "string", "update time"),
                        column("create_id", "long", "user id"),
                        column("update_id", "long", "user id"));
        //创建表实例
        Id database = table("datasource_test", "database table", syncDB, sd, "root", "External", databaseColumns);
        Id syncFolder = table("folder_test", "sync folder table", syncDB, sd, "root", "External", syncFolderColumns);
        
        
        List<Id> inputs = new ArrayList<Id>();
        inputs.add(syncFolder);
        List<Id> outputs = new ArrayList<Id>();
        outputs.add(database);
        
        Id process = process("atlasProcess","this is a process","zhouzhou",inputs,outputs);
        //创建视图实例

    }

    public void getEntity() throws AtlasServiceException {
        Referenceable referenceable = atlasClient.getEntity("1406ddd0-5d51-41d4-b174-859bd4f34a5b");
        System.out.println(InstanceSerialization.toJson(referenceable, true));
    }

}
