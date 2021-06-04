The purpose of this program is to create the notes.csv file.  If you can create the notes.csv file another way, 
then this program is not needed.

The format of notes.csv:  A *.csv file with three columns:
  1. First column is the address
  2. Second column is the color of the text, defaults to 'red'.  
  3. Third column is the note for that address.  
  Example:
     "297 Moraga Way","","Balanced owed: $110.00  "
     "241 Purple Glen Drive","green","Balanced owed: $110.00  "
     
     
 The input is two files (which are exported from QuickBooks Online):
   
   The first file is "Name Address.csv" which is a *.csv containing two columns:
     1. First column is the name
     2. Second column is the address
     Example:
      "Achanta, Srikar & Deepika Jyotula",263 Dondero Way
      "Adair, Joey & Nelson, Enid",265 El Portal Way
   
   The second file is "Names Dues Owed.csv" which is a *.csv containing two columns:
     1.  First column is the name which must match the name in "Name Address.csv"
     2.  Second column is the amount owed
     Example:
      "Achanta, Srikar & Deepika Jyotula",300.00
      "Adair, Joey & Nelson, Enid",80.00
      