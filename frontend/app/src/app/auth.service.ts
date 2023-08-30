import {Injectable} from '@angular/core';
import {CookieService} from "ngx-cookie";
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {BehaviorSubject, filter, first, map, Observable} from "rxjs";
import {isNonNull} from "../util";

export interface UserCredentials {
  name: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLoggedIn$ = new BehaviorSubject<boolean|null>(null);
  credentials$ = new BehaviorSubject<UserCredentials|null>(null);

  getIsLoggedIn() {
    return this.isLoggedIn$.pipe(filter(isNonNull), first())
  }

  getCredentials() {
    return this.credentials$.pipe(filter(isNonNull), first());
  }

  getAuthHeaders() {
    return this.getCredentials().pipe(map(this.authHeaders));
  }
  private authHeaders(credentials: UserCredentials) {
    return { "Authorization": `Basic ${btoa(credentials.name + ":" + credentials.password)}` };
  }

  constructor(private http: HttpClient, private cookies: CookieService) {
    const credentials = this.cookies.getObject("credentials") as UserCredentials | undefined;
    if (credentials) {
      this.login(credentials).subscribe(valid => {
        if (valid) this.credentials$.next(credentials);
        else this.cookies.remove("credentials");
      });
    } else {
      this.isLoggedIn$.next(false);
    }
  }

  register(credentials: UserCredentials) {
    return new Observable<boolean>(subscriber => {
      this.http.post(`${environment.root}/auth/register`, credentials, {responseType: "text"}).subscribe(() => {
        this.login(credentials).subscribe(subscriber);
      });
    });
  }

  login(credentials: UserCredentials) {
    return new Observable<boolean>(subscriber => {
      this.http.post<{valid: boolean}>(`${environment.root}/auth/validateCredentials`, credentials).subscribe(result => {
        subscriber.next(result.valid);
        this.isLoggedIn$.next(result.valid);
        if (result.valid) {
          this.credentials$.next(credentials);
          this.cookies.putObject("credentials", credentials);
        }
      });
    });
  }

  changePassword(newPassword: string) {
    return new Observable<void>(subscriber => {
      this.getCredentials().subscribe(credentials => {
        this.http.post(`${environment.root}/auth/changePassword`, {password: newPassword}, {responseType: "text", headers: this.authHeaders(credentials)}).subscribe(() => {
          const newCredentials = {
            name: credentials.name,
            password: newPassword,
          };
          this.credentials$.next(newCredentials);
          this.cookies.putObject("credentials", newCredentials);
          subscriber.next();
        });
      });
    });
  }

  logout() {
    this.isLoggedIn$.next(false);
    this.credentials$.next(null);
    this.cookies.remove("credentials");
  }

  deleteAccount() {
    return new Observable<void>(subscriber => {
      this.getCredentials().subscribe(credentials => {
        this.http.post(`${environment.root}/auth/deleteAccount`, null, {responseType: "text", headers: this.authHeaders(credentials)}).subscribe(() => {
          this.isLoggedIn$.next(false);
          this.credentials$.next(null);
          subscriber.next();
        });
      });
    });
  }
}
