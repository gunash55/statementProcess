import {Injectable} from '@angular/core';

@Injectable()
export class FileUtil{

  constructor() {}

  isCSVFile(file) {
    return file.name.endsWith(".csv");
  }
  
  readFromCSV(issueRecordsArray, tokenDelimeter) {
    var retArr = []
    for (let i = 1; i < issueRecordsArray.length; i++) {
      let data = issueRecordsArray[i].split(tokenDelimeter);
         let col = 
         {
           firstName:data[0].replace(/^"(.*)"$/, '$1'),
           surName:data[1].replace(/^"(.*)"$/, '$1'),
           issueCount:data[2].replace(/^"(.*)"$/, '$1'),
           dob:data[3].replace(/^"(.*)"$/, '$1')
          };      
        retArr.push(col);
      } 
    return retArr;
  }

  validateHeaders(origHeaders, fileHeaaders) {
    if (origHeaders.length != fileHeaaders.length) {
      return false;
    }

    var headerFlag = true;
    for (let j = 0; j < origHeaders.length; j++) {
      if (origHeaders[j] != fileHeaaders[j]) {
        headerFlag = false;
        break;
      }
    }
    return headerFlag;
  }

  getHeaderArray(issueRecordsArr, tokenDelimeter) {
    let headers = issueRecordsArr[0].split(tokenDelimeter);
    let headerArray = [];
    for (let j = 0; j < headers.length; j++) {
      headerArray.push(headers[j]);
    }
    return headerArray;
  }


}