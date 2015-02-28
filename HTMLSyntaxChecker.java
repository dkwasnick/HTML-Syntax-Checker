import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.*;

public class HTMLSyntaxChecker {
	// MAIN FUNCTION:
	// 1. Read number of lines in
	// 2. Read each line in, store all lines in an ArrayList of Strings
	// 3. Pass ArrayList to an evaluation function
	// 4. Repeat until you reach a 0
	
	// EVALUATION FUNCTION:
	// 1. Go line by line
	// 2. Use regex matcher to find instances of tags in current line
	// 3. Check each tag as it arrives to see that it is valid on its own
	// 4. Add opening tags to a stack of openTags
	// 5. Either pop the relevant openTag off the stack or return an error on each closing tag
	// 6. Upon reaching end of last line, return OK, or error if there are still open tags
	
	
	public static void main(String[] args)
	{
		// Keep track of test case
		int testCaseNumber = 0;
		boolean running = true;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (running)
		{
			try {
				// Read numLines
				int numLines = Integer.parseInt(br.readLine());
				
				if (numLines == 0)
				{
					// End
					running = false;
					return;
				}
				
				ArrayList<String> lines = new ArrayList<String>();
	
				// Read lines in
				for (int i=0; i<numLines; i++)
				{
					String s = br.readLine();
					lines.add(s);
				}
				
				// Evaluate HTML syntax of lines and print output
				testCaseNumber++;
				System.out.println("Test Case "+testCaseNumber);
				evaluateAndReport(lines);
				
				
			} catch (IOException e) {
				// Error reading lines
				System.err.println("Error reading lines from STDIN");
				e.printStackTrace();
			}
		}
		
		
	}
	
	// The evaluation function
	// Goes line by line and validates correctness
	// Prints to stdout with response to lines
	protected static void evaluateAndReport(ArrayList<String> lines)
	{
		// Keep track of opened tags, and remove them as they are closed
		Stack<String> openTags = new Stack<String>();
		
		// Go line by line
		for (int i=0; i<lines.size(); i++)
		{
			// Just used for outputting errors
			int lineNumber = i+1;
			
			
			// Parse out HTML tags
			// "<[\\S&&[^>]]*[>|$|\\s]" matches: '<', followed by any number of non-whitespace, non-'>' characters, followed by '>', or the end of line, or a whitespace character
			// if it DOES end with the end of the line or a whitespace character, it is invalid, obviously
			Pattern p = Pattern.compile("<[\\S&&[^>]]*(>|$|\\s)");
			Matcher m = p.matcher(lines.get(i));
			
			// For each tag found
			while (m.find())
			{
				// The most recent tag found
				String tag = m.group();
				
				// Make sure it closes correctly
				if (tag.charAt(tag.length()-1) != '>')
				{
					System.out.println("line "+lineNumber+": bad character in tag name.");
					return;
				}
				
				// Strip < and >
				tag = stripBrackets(tag);
				int validity = validateTag(tag);
				
				if (validity == 2)
				{
					// too many/few characters
					System.out.println("line "+lineNumber+": too many/few characters in tag name.");
					return;
				}
				
				if (validity == 1)
				{
					// bad character
					System.out.println("line "+lineNumber+": bad character in tag name.");
					return;
				}
				
				// Now, we know that the tag was valid and validity == 0
				
				// The tag is a closing tag
				if (isClosing(tag))
				{
					// If there are no remaining open tags
					if (openTags.isEmpty())
					{
						System.out.println("line "+lineNumber+": no matching begin tag.");
						return;
					}
					
					// If the most recently opened tag matches the closing tag
					if (openTags.peek().equals(stripClosing(tag)))
					{
						// Remove the open tag from the stack
						openTags.pop();
					}else{
						// The closing tag does not match the opening tag
						System.out.println("line "+lineNumber+": expecting </"+openTags.pop()+">");
						return;
					}	
				}else{
					// The tag is an opening tag, push it onto the stack
					openTags.push(tag);
				}
			}
			
		}
		
		// We have gone through every line, and haven't returned with failure yet
		// Make sure there are no remaining open tags
		if (openTags.isEmpty())
		{
			// Success! Return OK
			System.out.println("OK");
		}else{
			// There is still an open tag, return expecting open tag
			System.out.println("line "+lines.size()+": expected </"+openTags.pop()+">");
			return;
		}
		
	}
	
	// Returns whether a tag is valid or not
	// 0 = OK
	// 1 = bad character
	// 2 = too many/few characters
	protected static int validateTag(String tag)
	{
		// Eliminate empty tags immediately
		if (tag.length() == 0)
		{
			return 2;
		}
		
		// Is it a closing tag, or opening tag?
		int closing = 0;
		if (tag.charAt(0) == '/')
		{
			closing = 1;
		}
		
		// If the tag is too long		
		if (tag.length()-closing > 10)
		{
			return 2;
		}
		
		// If the tag is too short
		if (tag.length()-closing < 1)
		{
			return 2;
		}
		
		// Go through each character and make sure it's valid
		// Start at 'closing', so ignore the '/' if there is one
		for (int i=closing; i<tag.length(); i++)
		{
			// The character is not a valid uppercase character
			if (!Character.isUpperCase(tag.charAt(i)))
			{
				return 1;
			}
		}
		
		// Passed both of the other tests, tag is OK
		return 0;
	}
	

	// UTILITY FUNCTIONS

	// Strip the < and > from the tags
	protected static String stripBrackets(String tag)
	{
		return tag.substring(1, tag.length()-1);
	}
	
	// Return whether or not the tag is a closing tag
	protected static boolean isClosing(String tag)
	{
		return (tag.charAt(0) == '/');		
	}
	
	// Return the tag with the '/' stripped	
	protected static String stripClosing(String tag)
	{
		return tag.substring(1);
	}
	

}


