import {NgModule} from "@angular/core";
import { CommonModule } from '@angular/common';
import { LayoutModule } from '@angular/cdk/layout';
import {CdkTableModule} from '@angular/cdk/table';
import { 
    MatAutocompleteModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatStepperModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatTextareaAutosize
} from '@angular/material';

const modules = [
    CdkTableModule,
    CommonModule,
    MatToolbarModule, 
    MatButtonModule, 
    MatSidenavModule, 
    MatIconModule, 
    MatListModule,
    LayoutModule,
    MatTableModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSidenavModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTabsModule,
    MatCardModule,
    MatGridListModule,
    MatSnackBarModule
];

@NgModule({
imports: [...modules],
exports: [...modules],
})
export class CustomMaterialModule { }