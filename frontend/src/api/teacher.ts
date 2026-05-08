import request from '../utils/request'

/** 获取当前教师班级内所有待审核绑定申请 */
export const getPendingBinds = () =>
  request.get('/api/teacher/pending-binds')

/** 审核绑定（通过=1 / 拒绝=2），复用已有接口 */
export const auditBind = (bindId: number, status: number, auditorId: number) =>
  request.put(`/api/students/audit-bind/${bindId}`, { status, auditorId })
