import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { EmployeePair } from '../employee-pair';
import { ProjectService } from '../project/project.service';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {

  employeePairs!: EmployeePair[];
  employeePairsExist = false;
  isDisabled = false;
  listEmptyMessage = false;
  file: any;
  errorMessage:any = null;

  constructor(private projectService: ProjectService, private httpClient:HttpClient) { }

  ngOnInit(): void {
    
  }

  onFileSelect(event:any) {
    if (event.target.files.length > 0) {
      this.file = event.target.files[0];
    }
  }

  submitFile(){
    let formData = new FormData();
    formData.set("file", this.file);
    this.projectService.uploadFile(formData).subscribe((response) => {
      this.getPairs();
      this.isDisabled = true;
    },(error) => {
      this.errorMessage = error.message;
      this.isDisabled = true;
    });
  }

  getPairs(){
      this.projectService.getEmployeePairs().subscribe(pairs => {
        console.log(pairs.length);
        if(pairs.length!=0){
          this.employeePairs = pairs;
          this.employeePairsExist = true;
        }
        else{
          this.listEmptyMessage = true;
        }
    },(error) => {
      this.errorMessage = error.message;
      this.isDisabled = true;
    });
  }

  restart(){
    this.projectService.deleteAllProjects().subscribe(response => {
      this.employeePairsExist = false;
      this.employeePairs = [];
      this.isDisabled = false;
      this.errorMessage = null;
      this.listEmptyMessage = false;
      });
  }

}
