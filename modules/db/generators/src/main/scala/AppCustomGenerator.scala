package db.generators

import escalator.db.generators._

class AppCustomGenerator extends CustomGenerator 
{
	def setup(): Boolean = {
		true
	}

	def processFileData(fileData: String): String = {
		// fileData.replace("instrumentSymbol: ExchangeIdent","instrumentSymbol: InstrumentSymbol")
		fileData
			.replace("case class AttributeAttrType","//case class AttributeAttrType")
			.replace("AttributeAttrType", "AttributeType")
	}

	//# DO THIS IN A SHARED FILE as not its repeating code!
	def customMappers(tableClass: String) = {
		// """
		//   // CustomGenerator.customMappers - START
		//   implicit val attributeTypeEncoder: Encoder[AttributeType] = encoder(java.sql.Types.OTHER, (index, value, row) => {
		//       val pgObj = new org.postgresql.util.PGobject()
		//       pgObj.setType("attribute_type")
		//       pgObj.setValue(s"(${value.attr},${value.ident})")
		//       row.setObject(index, pgObj, java.sql.Types.OTHER)      
		//     }
		//   )

		//   implicit val attributeTypeDecoder: Decoder[AttributeType] = decoder((index, row, conn) => {
		//       val i = row.getString(index)
		//       val cleanedInput = i.trim.stripPrefix("(").stripSuffix(")")
		//       val parts = cleanedInput.split(",")        

		//       if (parts.size == 2){
		//         AttributeType(parts(0).trim, parts(1).trim)
		//       } else {
		//         null
		//       }
		//     }
		//   )
		//   // CustomGenerator.customMappers - END
		// """	
		""
	}

	def customTypes(): List[String] = {
		List()
	}


	def customMappings(): Map[String,String] = {
		Map(
    		// CUSTOM TYPES
    		"attribute_type" -> "com.escalatorstarter.models.AttributeType"	
		)
	}

	def useDefaultValue(tableName: String,columnName: String): Boolean = {
		// for coluns added after the initial creation we want to default them. auto do this.
		false
	}

}
