from contextlib import nullcontext
import PyPDF2
import textract

from fastapi import APIRouter
from server.models.resources import (
   ResponseModel, RequestResourceModel
)
import requests
from server.ai import getTheResource

router = APIRouter()

@router.post("/tools/generate",response_model=ResponseModel ,tags=["AI Resources"])
def generateResource(request: RequestResourceModel):
    #step 1 : access the resource from online
    downloaded = downloadResource(request.originalFileUrl,request.originalFilename)

    if not downloaded:
        return ResponseModel(None, 500, "Download Error", "Error Acessing the resource")
        #step 2: extract the text or send it to the ai like that
    else:
        try:
            text = extractPdfText(request.originalFilename)
            data = getTheResource(request.type.value, text, request.count, request.difficulty)
            return ResponseModel(data, 200, "Sucessful", "")
        except Exception as e:
            return ResponseModel(None, 500, "Error occured while processing",str(e))



def downloadResource(link:str,filename:str):

    response = requests.get(link)
    if response.status_code == 200:
        with open(filename, "wb") as file:
            file.write(response.content)
        print(f"File downloaded successfully as {filename}")
        return True
    else:
        print(f"Failed to download file. Status code: {response.status_code}")
        return False

def extractPdfText(filePath=''):
    print(f"Opening file at {filePath}")
    pdfFileReader = PyPDF2.PdfReader(filePath)
    totalPages = len(pdfFileReader.pages)
    print(f"This pdf contains {totalPages} pages.")

    currentPageNumber = 0
    text = ''

    while (currentPageNumber < totalPages):
        pdfPage = pdfFileReader.pages[currentPageNumber]
        text = text + pdfPage.extract_text()
        currentPageNumber += 1

    if (text == ''):
        text = textract.process(filePath, moethod='tesseract', encoding='utf-8')

    return text



