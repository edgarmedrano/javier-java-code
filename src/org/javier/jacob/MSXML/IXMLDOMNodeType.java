package org.javier.jacob.MSXML;

public interface IXMLDOMNodeType {
	/**
	 * The node represents an element. An element node can have the following
	 * child node types: Element, Text, Comment, ProcessingInstruction,
	 * CDATASection, and EntityReference.
	 * <p>
	 * An element node can be the child of the Document, DocumentFragment,
	 * EntityReference, and Element nodes.
	 * </p>
	 */
	final short NODE_ELEMENT = 1;

	/**
	 * The node represents an attribute of an element. An attribute node can
	 * have the following child node types: Text and EntityReference.
	 * <p>
	 * An attribute does not appear as the child node of any other node type;
	 * note that it is not considered a child node of an element.
	 * </p>
	 */
	final short NODE_ATTRIBUTE = 2;

	/**
	 * The node represents the text content of a tag. A text node cannot have
	 * any child nodes. A text node can appear as the child node of the
	 * Attribute, DocumentFragment, Element, and EntityReference nodes.
	 */
	final short NODE_TEXT = 3;

	/**
	 * The node represents a CDATA section in the XML source. CDATA sections are
	 * used to escape blocks of text that would otherwise be recognized as
	 * markup. A CDATA section node cannot have any child nodes. A CDATA section
	 * node can appear as the child of the DocumentFragment, EntityReference,
	 * and Element nodes.
	 */
	final short NODE_CDATA_SECTION = 4;

	/**
	 * The node represents a reference to an entity in the XML document. This
	 * applies to all entities, including character entity references. An entity
	 * reference node can have the following child node types: Element,
	 * ProcessingInstruction, Comment, Text, CDATASection, and EntityReference.
	 * An entity reference node can appear as the child of the Attribute,
	 * DocumentFragment, Element, and EntityReference nodes.
	 */
	final short NODE_ENTITY_REFERENCE = 5;

	/**
	 * The node represents an expanded entity. An entity node can have child
	 * nodes that represent the expanded entity (for example, Text and
	 * EntityReference nodes). An entity node can appear as the child of the
	 * DocumentType node.
	 */
	final short NODE_ENTITY = 6;

	/**
	 * The node represents a processing instruction from the XML document. A
	 * processing instruction node cannot have any child nodes. A processing
	 * instruction node can appear as the child of the Document,
	 * DocumentFragment, Element, and EntityReference nodes.
	 */
	final short NODE_PROCESSING_INSTRUCTION = 7;

	/**
	 * The node represents a comment in the XML document. A comment node cannot
	 * have any child nodes. A comment node can appear as the child of Document,
	 * DocumentFragment, Element, and EntityReference nodes.
	 */
	final short NODE_COMMENT = 8;

	/**
	 * The node represents a document object, which, as the root of the document
	 * tree, provides access to the entire XML document. It is created using the
	 * progID "Msxml2.DOMDocument", or through a data island using <XML> or
	 * <SCRIPT LANGUAGE=XML>. A document node can have the following child node
	 * types: Element (maximum of one), ProcessingInstruction, Comment, and
	 * DocumentType. A document node cannot appear as the child of any node
	 * types.
	 */
	final short NODE_DOCUMENT = 9;

	/**
	 * The node represents the document type declaration, indicated by the
	 * <!DOCTYPE > tag. A document type node can have the following child node
	 * types: Notation and Entity. A document type node can appear as the child
	 * of the Document node.
	 */
	final short NODE_DOCUMENT_TYPE = 10;

	/**
	 * The node represents a document fragment. A document fragment node
	 * associates a node or subtree with a document without actually being
	 * contained within the document. A document fragment node can have the
	 * following child node types: Element, ProcessingInstruction, Comment,
	 * Text, CDATASection, and EntityReference. A DocumentFragment node cannot
	 * appear as the child of any node types.
	 */
	final short NODE_DOCUMENT_FRAGMENT = 11;

	/**
	 * A node represents a notation in the document type declaration. A notation
	 * node cannot have any child nodes. A notation node can appear as the child
	 * of the DocumentType node.
	 */
	final short NODE_NOTATION = 12;
}
