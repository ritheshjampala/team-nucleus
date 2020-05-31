import { Component, OnInit } from '@angular/core';
import { PatientDetailsComponent } from '../patient-details/patient-details.component' 

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  constructor(private patientDetails: PatientDetailsComponent) { }

  ngOnInit() {
  }

  showAddPatientDetailsDialogFromFooter() {
    this.patientDetails.showAddPatientDetailsDialog();
  }

}
