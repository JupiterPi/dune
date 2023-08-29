import {Injectable} from '@angular/core';
import {CookieService} from "ngx-cookie";
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";

export interface UserCredentials {
  name: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  credentials?: UserCredentials;
  private setCredentials(credentials: UserCredentials) {
    this.credentials = credentials;
    this.cookies.putObject("credentials", credentials);
  }

  get loggedIn() {
    return this.credentials != undefined;
  }
  get authHeaders() {
    return this.credentials != undefined ? {
      "Authorization": `Basic ${btoa(this.credentials!!.name + ":" + this.credentials!!.password)}`
    } : undefined;
  }

  constructor(private http: HttpClient, private cookies: CookieService) {
    const credentials = this.cookies.getObject("credentials") as UserCredentials | undefined;
    if (credentials) {
      this.login(credentials).subscribe(valid => {
        if (!valid) this.cookies.remove("credentials");
      });
    }
  }

  login(credentials: UserCredentials) {
    return new Observable<boolean>(subscriber => {
      this.http.post<{valid: boolean}>(`${environment.root}/auth/validateCredentials`, credentials).subscribe(result => {
        subscriber.next(result.valid);
        if (result.valid) this.setCredentials(credentials);
      });
    });
  }

  changePassword(newPassword: string) {
    return new Observable<void>(subscriber => {
      this.http.post(`${environment.root}/auth/changePassword`, {password: newPassword}, {responseType: "text", headers: this.authHeaders}).subscribe(() => {
        this.setCredentials({
          name: this.credentials!!.name,
          password: newPassword,
        });
        subscriber.next();
      });
    });
  }
}
