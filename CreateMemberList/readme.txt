The purpose of CreateMemberList is to take member names from "Personnel File.csv", member pictures from
"Face Pictures.zip", (optional) notes for a household from "notes.csv", and combine that information to
produce the members.html file.

Input:
  The "Personnel File.csv" is exported from VsAxess get management software.  Only the first three columns are
  used:
    1:  The first column is a numeric ID that matches to the pictures in "Face Pictures.zip".
    2.  The second column is used to get the home address
    3.  The third column is the member name
    
    The "Face Pictures.zip" is exported from VsAxess get management software.  The pictures are named with
    the numeric ID that is in the first column of "Personnel File.csv".
    
    The "notes.csv" file is optional.  Any notes contained in that file will be connected to the address in the 
    final members.html file. The format of notes.csv is a csv file with three columns:
      1. First column is the address
      2. Second column is the color of the text, defaults to 'red'.  
      3. Third column is the note for that address.  