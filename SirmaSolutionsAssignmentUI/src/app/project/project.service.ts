import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EmployeePair } from '../employee-pair';
import { ProjectHistory } from '../project-history';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private http:HttpClient) { }
  baseURL = "http://localhost:8080/sirma"

  uploadFile(file: FormData): Observable<ProjectHistory[]>{
    return this.http.post<ProjectHistory[]>(this.baseURL + "/upload", file);
  }

  getEmployeePairs(): Observable<EmployeePair[]>{
    return this.http.get<EmployeePair[]>(this.baseURL + "/employees");
  }

  deleteAllProjects():Observable<Object>{
    return this.http.delete(this.baseURL + "/restart");
  }
}
