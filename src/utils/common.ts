export function timestampToTime(timestamp: number): string {
  const date = new Date(timestamp); // 时间戳已经是毫秒级别，无需乘以1000
  const year = date.getFullYear();
  const month = ('0' + (date.getMonth() + 1)).slice(-2); // 月份从0开始，需要加1
  const day = ('0' + date.getDate()).slice(-2);
  const hours = ('0' + date.getHours()).slice(-2);
  const minutes = ('0' + date.getMinutes()).slice(-2);
  const seconds = ('0' + date.getSeconds()).slice(-2);

  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

export const stringEmpty = (str: string): boolean => {
    return str == undefined || str == "" || str == null;
}