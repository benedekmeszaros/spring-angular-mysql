import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, map, switchMap, throwError } from 'rxjs';
import { AuthService } from '../service/auth.service';
import { StorageService } from '../service/storage.service';

let skipIntercept: boolean = false;

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const auth: AuthService = inject(AuthService);
  const storage: StorageService = inject(StorageService);
  const handle403Error = (req: HttpRequest<any>, next: HttpHandlerFn) => {

    return auth.refresh()
      .pipe(
        switchMap((token) => {
          storage.setToken(token);
          return next(req.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          }));
        }),
        catchError((err) => {
          return throwError(() => err);
        })
      );
  };

  return next(req).pipe(map((event: HttpEvent<any>) => {
    if (event instanceof HttpResponse)
      skipIntercept = false;
    return event;
  }),
    catchError((err) => {
      if (err instanceof HttpErrorResponse && err.status === 403 && !skipIntercept) {
        skipIntercept = true;
        return handle403Error(req, next);
      }
      else {
        skipIntercept = false;
        return throwError(() => err);
      }
    }));
};
