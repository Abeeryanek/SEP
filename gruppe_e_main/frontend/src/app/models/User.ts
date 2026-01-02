
import { UserRole } from './UserRole';

export interface User {
  id?: number;
  username: string;
  firstname: string;
  lastname: string;
  email: string;
  birthdate: string;
  role: UserRole;
  carType?: string;
  password: string;
}



