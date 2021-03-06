package components;

import org.cmg.jresp.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.knowledge.ts.*;
import org.cmg.jresp.comp.*;
import behaviour.*;
import policy.*;

@SuppressWarnings("unused")
public class Node_c1 extends Node {

	public Node_c1( RESPElementFactory elementFactory ) {
		super( "c1", new TupleSpace() );
//Interface Features	
this.addAttribute(new Attribute("id", ("c1")));
this.addAttribute(new Attribute("role", ("client")));
this.addAttribute(new Attribute("level", 1));
this.addAttribute(new Attribute("locality", ("UNIFI")));
this.addAttribute(new Attribute("load", 60));
//Knowledge	
this.put( new Tuple(  ("taskId"),  0 ) );
this.addAgent( new Client( ) );
	//Policy
	this.setPolicy(PClient.getAutomaton());
	}
}
