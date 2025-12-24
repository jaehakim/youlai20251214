import request from "@/utils/request";

const FileAPI = {
  /** 파일 업로드 (FormData 전달, 업로드 진행률 콜백) */
  upload(formData: FormData, onProgress?: (percent: number) => void) {
    return request<any, FileInfo>({
      url: "/api/v1/files",
      method: "post",
      data: formData,
      headers: { "Content-Type": "multipart/form-data" },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress?.(percent);
        }
      },
    });
  },

  /** 파일 업로드 (File 전달) */
  uploadFile(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return request<any, FileInfo>({
      url: "/api/v1/files",
      method: "post",
      data: formData,
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  /** 파일 삭제 */
  delete(filePath?: string) {
    return request({
      url: "/api/v1/files",
      method: "delete",
      params: { filePath },
    });
  },

  /** 파일 다운로드 */
  download(url: string, fileName?: string) {
    return request({
      url,
      method: "get",
      responseType: "blob",
    }).then((res) => {
      const blob = new Blob([res.data]);
      const a = document.createElement("a");
      const urlObject = window.URL.createObjectURL(blob);
      a.href = urlObject;
      a.download = fileName || "다운로드 파일";
      a.click();
      window.URL.revokeObjectURL(urlObject);
    });
  },
};

export default FileAPI;

export interface FileInfo {
  name: string;
  url: string;
}
