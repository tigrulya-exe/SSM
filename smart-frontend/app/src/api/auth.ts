import { httpClient } from './httpClient';

export class AuthApi {
  public static async login(username: string, password: string) {
    const response = await httpClient.post('/api/v1/login', {
      username,
      password,
    });

    return response.data;
  }

  public static async logout() {
    const response = await httpClient.post('/api/v1/logout');
    return response.data;
  }

  public static async checkSession() {
    const response = await httpClient.post<{ name: string }>('/api/v1/currentUser');

    return response.data;
  }
}
