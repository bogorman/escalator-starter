package com.escalatorstarter.models

//manually generated file

// case class AttributeType(
// 	attr: String,
// 	ident: String
// )

// case class AttributeAttrType(
// 	attr: String,
// 	ident: String
// )


trait BaseAttributeType{
	def attr: String
	def ident: String
}

case class AttributeType(
	attr: String,
	ident: String
) extends BaseAttributeType
