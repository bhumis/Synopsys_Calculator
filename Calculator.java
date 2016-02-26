public class Calculator{

	static boolean exceptionCaught = false;

	static boolean infoEnabled = false;

	static boolean errorEnabled = false;

	static boolean debugEnabled = false;

	public static void setInfoOption(String s)
	{
		if (s.compareToIgnoreCase("-- info") == 0)
			infoEnabled = true;
	}

	public static void setErrorOption(String s)
	{
		if (s.compareToIgnoreCase("-- error") == 0)
			errorEnabled = true;
	}

	public static void setDebugOption(String s)
	{
		if (s.compareToIgnoreCase("-- debug") == 0)
			debugEnabled = true;
	}

	public static void logError(String s)
	{
		if (errorEnabled)
			System.err.println("Error: " + s);
		else
			System.out.print("");
	}

	public static void logInfo(String s)
	{
		if (infoEnabled)
			System.err.println("Info: " + s);
		else
			System.out.print("");
	}

	public static void logDebug(String s)
	{
		if (debugEnabled)
			System.out.println("Debug: " + s);
		else
			System.out.print("");
	}

	public static String add(String arg1, String arg2)
	{
		logDebug("Calling add with parameter " + arg1 + " and " + arg2);
		Integer res=0;
		try
		{
			res = Integer.parseInt(arg1) + Integer.parseInt(arg2);
		}
		catch(Exception e)
		{
			logError("Exception during add "+ e.getLocalizedMessage());
			exceptionCaught = true;
		}
		return res.toString();
	}

	public static String sub(String arg1, String arg2)
	{
		logDebug("Calling sub with parameter " + arg1 + " and " + arg2);
		Integer res=0;
		try
		{
			res = Integer.parseInt(arg1) - Integer.parseInt(arg2);
		}
		catch(Exception e)
		{
			logError("Exception during sub "+ e.getLocalizedMessage());
			exceptionCaught = true;
		}
		return res.toString();	
	}

	public static String mult(String arg1, String arg2)
	{
		logDebug("Calling mult with parameter " + arg1 + " and " + arg2);
		Integer res=0;
		try
		{
			res = Integer.parseInt(arg1) * Integer.parseInt(arg2);
		}
		catch(Exception e)
		{
			logError("Exception during mult "+ e.getLocalizedMessage());
			exceptionCaught = true;
		}
		return res.toString();
	}

	public static String div(String arg1, String arg2)
	{
		logDebug("Calling div with parameter " + arg1 + " and " + arg2);
		Integer res=0;
		try
		{
			res = Integer.parseInt(arg1) / Integer.parseInt(arg2);
		}
		catch(Exception e)
		{
			logError("Exception during div "+ e.getLocalizedMessage());
			exceptionCaught = true;
		}
		return res.toString();
	}

	// Function takes input String command and evaluates its value
	public static void parseString(String command) 
	{
		if (!command.contains(","))
		{
			// No possible operations left, now get int value
			logDebug("Evaluating value of parsed string");
			Integer num=0;
			try
			{
				num = Integer.parseInt(command);
			}
			catch (Exception e)
			{
				// Catch for invalid numeric input
				logError("Exception during parsing " + e.getLocalizedMessage());
				exceptionCaught = true;
				return;
			}
			
			logInfo("Output returned is " + num.toString());

			// Always print output
			System.out.println(num);
		}
		else 
		{
			// command has possible operations to be performed
			do {
				int index;
				String newCommand="";

				// Search through command for good evaluation point
				for (index=1; index<command.length(); index++)
				{
					// 1) Look for ','
					if (command.charAt(index) == ',' )
					{
						String arg1="", arg2="";
						Character c1 = command.charAt(index-1);
						Character c2 = command.charAt(index+1);

						// 2) Compare if we have numbers before and after ','
						if (Character.isDigit(c1) && 
						   (Character.isDigit(c2) || (Character.compare(c2, '-') == 0)))
						{
							logDebug("Reached a good evaluation point");

							// 3) Now get arguments from left and right of this index.
							int low,high;
							for (low=index-2; low>0; low--)
							{
								if (command.charAt(low) == '(')
									break;
							}
							arg1 = command.substring(low+1, index);

							for(high=index+2; high<command.length(); high++)
							{
								if (command.charAt(high) == ')')
									break;
							}
							arg2 = command.substring(index+1, high);

							String tempCommand = command.substring(0, low);
							String res = "";

							// 4) Get action to be performed on the obtained arguments
							if (tempCommand.endsWith("add"))
							{
								res = add(arg1,arg2);
								if (exceptionCaught)
									return;
								low=low-4;

							}
							else if (tempCommand.endsWith("sub"))
							{
								res = sub(arg1,arg2);
								if (exceptionCaught)
									return;
								low=low-4;
							}
							else if (tempCommand.endsWith("mult"))
							{
								res = mult(arg1,arg2);
								if (exceptionCaught)
									return;
								low=low-5;
							}
							else if (tempCommand.endsWith("div"))
							{
								res = div(arg1,arg2);
								if (exceptionCaught)
									return;
								low=low-4;
							}

							// 5) Form new command by replacing expression we just evaluated by its value
							if (low>=0)
							{					
								newCommand = command.substring(0, low+1);
							}
							newCommand = newCommand + res;

							if (high < command.length()-1)
							{
								newCommand = newCommand + command.substring(high+1, command.length());
							}

							logDebug("New command after evaluation is " + newCommand);
							
							// 6) Call again with newly obtained command.
							parseString(newCommand);
							break;
						}	
					}
				} 

				// Traversed till end, string does not have evaluation point.
				// test if it has let statements
				if (index==command.length())
				{	
					newCommand="";
					logDebug("Traversing for possible let statements in " + command);

					for (int ind=0; ind<command.length(); ind++)
					{
						// 1) Traverse for let command
						String commandSubSeq = command.substring(ind, command.length());
						if (commandSubSeq.startsWith("let"))
						{
							// Found let, now get variable and value
							String variable="", value="";
							int temp1 = ind+4;

							while (command.charAt(temp1) != ',')
							{
								// 2) Get variable
								variable = variable + command.charAt(temp1);
								temp1++;
							}
							temp1++;

							while (command.charAt(temp1) != ',')
							{
								// 3) Get value
								value = value + command.charAt(temp1);
								temp1++;
							}

							Character c = value.charAt(0);	
							// 4) Compare if value is a num
							if (Character.isDigit(c) || (Character.compare(c, '-') == 0))
							{
								//We have reached executable let
								logDebug("Evaluating a let statement");

								if (ind>0)
								{
									// 5) command before let remains same
									newCommand = command.substring(0, ind);
								}

								String replacedRemainingCmd = command.substring(temp1+1, command.length());

								// 6) Find variable pattern to be replaced
								
								// Replace these patterns in remaining command
								String pattern1 = "\\(" + variable + ',';
								String pattern2 = ',' + variable + "\\)";

								// Replace with these strings in remaining command
								String replacement1 = '(' + value + ',';
								String replacement2 = ',' + value + ')';

								String replacedReamintemp = "";
								if (replacedRemainingCmd.startsWith("let"))
								{
									newCommand = newCommand + "let";
									replacedRemainingCmd = replacedRemainingCmd.substring(3, replacedRemainingCmd.length());
								}

								if (replacedRemainingCmd.contains("let"))
								{
									// 7) If remaining command contains more let statements,
									//    skip replacing variable
									int ind1 = replacedRemainingCmd.indexOf("let");
									replacedReamintemp = replacedRemainingCmd.substring(ind1, replacedRemainingCmd.length());
									replacedRemainingCmd = replacedRemainingCmd.substring(0, ind1);
								}
								
								try
								{
									// 8) Replace variable with value
									logDebug("Replacing variable "+ variable + " with value "+ value);
									replacedRemainingCmd = replacedRemainingCmd.replaceAll(pattern1, replacement1);
									replacedRemainingCmd = replacedRemainingCmd.replaceAll(pattern2, replacement2);	
									replacedRemainingCmd = replacedRemainingCmd + replacedReamintemp;
									replacedRemainingCmd = replacedRemainingCmd.replaceFirst("\\)\\)", "\\)");	
								}
								catch (Exception e)
								{
									// 9) Error out for not matching regex, possibly invalid input
									logError("Exception during parse: " + e.getLocalizedMessage());
									exceptionCaught = true;
									return;
								}

								newCommand = newCommand + replacedRemainingCmd;
								logDebug("New command after processing let is " + newCommand);
								break;								
							}					
						}
					}
					// 10) Parse again with new command formed after processing let.
					parseString(newCommand);
				}
			}while(false);
		}
	}

	public static void main(String[] args) 
	{
		String input = args[0];
		
		setInfoOption(args[1]);
		setErrorOption(args[2]);
		setDebugOption(args[3]);

		input = input.replaceAll(" ", "");
		logInfo("String to be parsed after eliminating spaces is " + input);
		parseString(input);

		if (exceptionCaught)
		{
			logError("Invalid input, Please enter in correct format");
		}
	}
}