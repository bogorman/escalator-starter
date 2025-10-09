package db.generators

import escalator.db.generators._
import escalator.util._

class AppCustomGenerator extends CustomGenerator 
{
	def setup(): Boolean = {
		TextUtil.CUSTOM_PLURAL_MAPPINGS += ("entity" -> "entities")
		TextUtil.CUSTOM_PLURAL_MAPPINGS += ("Entity" -> "Entities")		
		true
	}

	def processFileData(fileData: String): String = {
		fileData
	}

	//# DO THIS IN A SHARED FILE as not its repeating code!
	def customMappers(tableClass: String) = {
		""
	}

	def customTypes(): List[String] = {
		List()
	}

	def columnAttributeTypeMappings(): Map[String,String] = {
		Map()
	}

	def customMappings(): Map[String,String] = {
		Map(
    		// CUSTOM TYPES
    		"attribute_type" -> "com.escalatorstarter.models.AttributeType",
    		// Value types - map database string types to custom Scala types
		)
	}

	def useDefaultValue(tableName: String,columnName: String): Boolean = {
		// for coluns added after the initial creation we want to default them. auto do this.
		false
	}
	

}
