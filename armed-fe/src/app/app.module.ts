import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {MatFormFieldModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CustomMaterialModule } from "./material.module";
import { AppComponent } from './app.component';
import { PatientDetailsComponent, DialogAddPatientDetailsDialog } from './patient-details/patient-details.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {SlideshowModule} from 'ng-simple-slideshow';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    PatientDetailsComponent,
    DialogAddPatientDetailsDialog,
    NavbarComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    CustomMaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SlideshowModule
  ],
  providers: [PatientDetailsComponent],
  bootstrap: [AppComponent],
  entryComponents: [DialogAddPatientDetailsDialog]
})
export class AppModule { }
