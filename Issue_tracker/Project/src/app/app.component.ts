import {Component, OnInit, ViewChild} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {FileUtil} from './file.util';
import {Constants} from './app.constants';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

/*
export class AppComponent {
title = 'RABO Bank';
}
*/
export class AppComponent implements OnInit {
  @ViewChild('fileUpload')
  fileUpload: any;
  issueRecords = [];
  private _fileUtil  = new FileUtil()

  ngOnInit() {}

  // METHOD CALLED WHEN CSV FILE IS IMPORTED
  fileUploadListener($event): void {
    var text = [];
    var files = $event.srcElement.files;

    if (Constants.isHeaderAndRecordLength) {
      if (!this._fileUtil.isCSVFile(files[0])) {
        alert("Restricted to .csv files only!");
        this.fileReset();
      }
    }

    var input = $event.target;
    var csvFile = new FileReader();
    csvFile.readAsText(input.files[0]);

    csvFile.onload = (data) => {
      let csvData = csvFile.result;
      let issueRecordsArray = csvData.split(/\r\n|\n/);

      var headerLength = -1;
      if (Constants.isHeaderPresent) {
        let headersRow = this._fileUtil.getHeaderArray(issueRecordsArray, Constants.delimeter);
        headerLength = headersRow.length;
      }
      this.issueRecords = this._fileUtil.readFromCSV(issueRecordsArray, Constants.delimeter);

      if (this.issueRecords == null) {
        //If control reached here it means csv file contains error, reset file.
        this.fileReset();
      }
    }

    csvFile.onerror = function() {
    };
  };

  fileReset() {
    this.fileUpload.nativeElement.value = "";
    this.issueRecords = [];
  }
}

