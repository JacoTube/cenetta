export interface UserDto { id: number; username: string; displayName: string; }

export type TaskStatus = 'PENDING_APPROVAL' | 'FREE' | 'ASSIGNED' | 'COMPLETED' | 'REJECTED';

export interface Task {
  id: number;
  title: string;
  note: string | null;
  status: TaskStatus;
  assignee: UserDto | null;
  proposedBy: UserDto | null;
}

export interface EventDetail {
  id: number;
  title: string;
  description: string | null;
  eventDateTime: string;
  deadline: string;
  inviteCode: string;
  organizer: UserDto;
}

export interface AuthResponse { 
    token: string; 
    user: UserDto; 
}

export interface EventSummary {
  id: number;
  title: string;
  eventDateTime: string;
  deadline: string;
  closed: boolean;
  organizer: boolean;
  organizerName: string;
  participantCount: number;
}