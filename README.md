# account-pdf2table

## Purpose

Read a PDF for a bank account and extract the table of the operations.

The purpose is to copy/paste these operations into Excel/Calc file to be imported into an accounting software like Inqom.

The 1st version was to copy/paste manually the PDF from Chrome to the application, and parse the text.

Now it can
- read directly the PDF and its folder
- extract the pdf as text, keeping the layout using the pdfbox library
- be configured to find the table columns 'date', 'description', 'debit', 'credit'
- and get operations in a table you can copy in clipboard to paste it in Excel.

Future feature: be able to open a PDF that is a scanned (aka an image) and use OCR to get the table.

## Note
- It uses Netbeans JPanel editor to create the panels.
- It needs to be cleanup !


