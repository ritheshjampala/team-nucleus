import { Component, OnInit, Inject} from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA, MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatTableDataSource, MatSnackBar, MatSnackBarConfig } from '@angular/material';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';


@Component({
  selector: 'app-patient-details',
  templateUrl: './patient-details.component.html',
  styleUrls: ['./patient-details.component.css']
})
export class PatientDetailsComponent implements OnInit {
  image: string = '../assets/img/Health.jpg';
  photo = ["../assets/img/Health.jpg","../assets/img/health2.png","../assets/img/health3.png"];
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json', "Access-Control-Allow-Credentials": "true", }),
    withCredentials: true
  };
  constructor(private http: HttpClient, public dialog: MatDialog) { }

  ngOnInit() {
  }

  showAddPatientDetailsDialog(): void {
    this.openAddPatientDetailsDialog({});
  }
  /*  
  This method opens the add plugin dialog and subscribes for the result after the plugin is added to the database
  */
 openAddPatientDetailsDialog(data): void {
    let dialogRef = this.dialog.open(DialogAddPatientDetailsDialog, {
      width: '600px',
      height: '400px',
      data: data
    });
    dialogRef.afterClosed().subscribe(result => {
      this.generateHttpReq(result).subscribe(result => {
      })
       
    });
  }

  generateHttpReq(details) {
    let body = JSON.stringify(details);
    console.log(environment.backendUrl+"patientDetails");
    return this.http.post<string>(environment.backendUrl + "mainController/patientDetails", body, this.httpOptions);
  }

}

@Component({
  selector: 'dialog-add-patient-details-dialog',
  templateUrl: 'dialog-add-patient-details-dialog.html',
})
export class DialogAddPatientDetailsDialog {
  
  
  constructor(
    public dialogRef: MatDialogRef<DialogAddPatientDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }
   
  onNoClick(): void {
    this.dialogRef.close();
  }
}
