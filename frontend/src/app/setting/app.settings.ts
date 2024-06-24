import { HttpHeaders } from "@angular/common/http";

export class AppSettings {
    public static API_URL = "http://localhost:7777";

    public static getBearerHeaders(token: string): HttpHeaders {
        return new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        });
    }
}
